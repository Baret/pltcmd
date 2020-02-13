package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.extensions.lowerOrEqual
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import de.gleex.pltcmd.model.world.MainCoordinate
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Creates a number of mountain tops and slowly decreases the terrain around them.
 */
class MountainTopHeightMapper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {
    companion object {
        // TODO: make max (and maybe also min) values a range, depending on the context
        private val MAX_TERRAIN = TerrainHeight.MAX
        private val MIN_TERRAIN = TerrainHeight.FOUR
    }

    private val log = LoggerFactory.getLogger(this::class)

    // TODO: More values depending on the context
    private val mountainTopsPerMainCoordinate: Int = 3
    /**
     * x % of main coordinates are picked to put a mountain in them
     */
    private val mainCoordinateQuotaForMountains = 0.25
    private val steepness = 0.55

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        // pick random positions for mountain tops
        val mountainTopLocations = findMountainTops(area, terrainMap)
        val frontier = mutableSetOf<Coordinate>()
        // set those to max height
        mountainTopLocations.forEach {
            terrainMap[it] = MAX_TERRAIN
            terrainMap[it] = TerrainType.MOUNTAIN
            frontier.add(it)
            context.mountainTops.addTarget(it)
        }
        // from each position find the four neighbours that have no height yet
        generateMountains(frontier, terrainMap)
        log.debug("Processed ${processedTiles.size} tiles to create ${mountainTopLocations.size} mountains")
    }

    private fun generateMountains(initialFrontier: Set<Coordinate>, terrainMap: MutableWorld) {
        var targetDistance = 0
        workFrontier(
            initialFrontier,
            { currentCoordinate: Coordinate ->
                val currentHeight: TerrainHeight? = terrainMap.heightAt(currentCoordinate)
                if (currentHeight != null && currentHeight > MIN_TERRAIN) {
                    terrainMap.
                        neighborsOf(currentCoordinate).
                        filter {
                            it.isNotProcessed()
                        }.
                        forEach { neighbor ->
                            terrainMap[neighbor] = currentHeight.lowerOrEqual(rand, steepness)
                            // TODO: Do not set terrainType in HeightMapper
                            // for better visibility setting a uniform terrainType. But later this will be done
                            // by another intermediate generator
                            terrainMap[neighbor] = TerrainType.MOUNTAIN
                            neighbor.addToNextFrontier()
                            context.mountainTops.add(neighbor, currentCoordinate, targetDistance)
                        }
                }
            },
            { targetDistance++ })
    }

    private fun findMountainTops(area: CoordinateArea, terrainMap: MutableWorld): Set<Coordinate> {
        val mainCoordinates = terrainMap.mainCoordinatesEmpty.
            ifEmpty {
                log.debug("No empty space found for mountains!")
                return emptySet()
            }
        val mountainTopAreasToFind = (mainCoordinates.size.toDouble() * mainCoordinateQuotaForMountains).toInt()
        log.debug("Trying to find $mountainTopAreasToFind areas for mountain tops in ${mainCoordinates.size} main coordinates")
        val pickedAreas = mutableSetOf<MainCoordinate>()
        var tries = 0
        do {
            val candidate = mainCoordinates.random(rand)
            if(candidate.toCoordinate() in area) {
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
