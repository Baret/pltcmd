package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.MainCoordinate
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Creates a number of mountain tops and slowly decreases the terrain around them.
 */
class MountainTopHeightMapper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator {
    companion object {
        private val log = LoggerFactory.getLogger(this::class)
        // TODO: make max (and maybe also min) values a range, depending on the context
        private val MAX_TERRAIN = TerrainHeight.MAX
        private val MIN_TERRAIN = TerrainHeight.FOUR
    }

    // TODO: More values depending on the context
    private val mountainTopsPerMainCoordinate: Int = 2
    /**
     * x % of main coordinates are picked to put a mountain in them
     */
    private val mainCoordinateQuotaForMountains = 0.05
    private val steepness = 0.60

    override fun generateArea(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: MutableWorld) {
        // pick random positions for mountain tops
        val mountainTopLocations = findMountainTops(bottomLeftCoordinate, topRightCoordinate, terrainMap)
        val processedTiles = mutableSetOf<Coordinate>()
        val frontier = mutableSetOf<Coordinate>()
        // set those to max height
        mountainTopLocations.forEach {
            terrainMap[it] = MAX_TERRAIN
            terrainMap[it] = TerrainType.MOUNTAIN
            frontier.add(it)
            context.mountainTops.addTarget(it)
        }
        // from each position find the four neighbours that have no height yet
        generateMountains(frontier, terrainMap, processedTiles)
        log.debug("Processed ${processedTiles.size} tiles to create ${mountainTopLocations.size} mountains")
    }

    private fun generateMountains(frontier: MutableSet<Coordinate>, terrainMap: MutableWorld, processedTiles: MutableSet<Coordinate>) {
        val executor = Executors.newFixedThreadPool(10)
        var targetDistance = 0
        // TESTING
        var lastCoord = Coordinate(0,0)
        while (frontier.isNotEmpty()) {
            val newFrontier = mutableSetOf<Coordinate>()
                frontier.forEach { currentCoordinate ->
//                    executor.execute {
                        val currentHeight = terrainMap.heightAt(currentCoordinate)!!
                        if (currentHeight > MIN_TERRAIN) {
                            val neighbors = terrainMap.neighborsOf(currentCoordinate)
                            val unprocessedNeighbors = neighbors.filter {
                                processedTiles.contains(it).not()
                            }
                            unprocessedNeighbors.forEach { neighbor ->
                                terrainMap[neighbor] = lowerOrEqualThan(currentHeight)
                                // for better visibility setting a uniform terrainType. But later this will be done
                                // by another intermediate generator
                                terrainMap[neighbor] = TerrainType.MOUNTAIN
                                newFrontier.add(neighbor)
                                context.mountainTops.add(neighbor, currentCoordinate, targetDistance)
                                // TESTING
                                lastCoord = neighbor
                            }
//                        }
                    }
            }
            processedTiles.addAll(frontier)
            frontier.clear()
            frontier.addAll(newFrontier)
            targetDistance++
            if (processedTiles.size > 400 && processedTiles.size % 500 < 100) {
                log.debug("Processed ${processedTiles.size} tiles. Max height to descend from is currently ${frontier.map { c -> terrainMap.terrainMap[c]!!.first!! }.map { it.value }.max()}")
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdown()

        // TESTING
        log.debug("Getting path from $lastCoord to closest mountain top and 'painting' it with HILL")
        val path = context.mountainTops.pathFrom(lastCoord).fold({"not found!"}) {
            it.forEach { c -> terrainMap[c] = TerrainType.HILL }
            it.joinToString(" -> ")
        }
        log.debug("The (probably) longest path has distance ${context.mountainTops.distanceFrom(lastCoord)} and is: $path")
        val randomCoord = processedTiles.random()
        log.debug("painting a path with length ${context.mountainTops.distanceFrom(randomCoord).get()} from random coordinate $randomCoord to closest mountain top with GRASSLAND")
        context.mountainTops.pathFrom(randomCoord).ifPresent {
            it.forEach { c -> terrainMap[c] = TerrainType.GRASSLAND }
        }
    }

    private fun lowerOrEqualThan(height: TerrainHeight): TerrainHeight {
        return if(rand.nextDouble() < steepness) {
            height - 1
        } else {
            height
        }
    }

    private fun findMountainTops(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: MutableWorld): Set<Coordinate> {
        val mainCoordinates = terrainMap.mainCoordinates
        // lets create mountain tops in about 10% of the map
        val mountainTopAreasToFind = (mainCoordinates.size.toDouble() * mainCoordinateQuotaForMountains).toInt()
        log.debug("Trying to find $mountainTopAreasToFind areas for mountain tops in ${mainCoordinates.size} main coordinates")
        val pickedAreas = mutableSetOf<MainCoordinate>()
        var tries = 0
        do {
            val candidate = mainCoordinates.random(rand)
            if(candidate.toCoordinate() in bottomLeftCoordinate..topRightCoordinate) {
                pickedAreas.add(candidate)
            }
            tries++
        } while (pickedAreas.size < mountainTopAreasToFind && tries < 1000)

        val pickedLocations = mutableSetOf<Coordinate>()
        pickedAreas.forEach { mainCoordinate ->
            for(i in 0 until mountainTopsPerMainCoordinate)
            pickedLocations.add(
                    mainCoordinate.
                            toCoordinate().
                            withRelativeEasting(rand.nextInt(100)).
                            withRelativeNorthing(rand.nextInt(100)))
        }
        log.debug("Found mountain top locations ${pickedLocations.sorted()}")
        return pickedLocations.toSet()
    }
}
