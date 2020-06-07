package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.extensions.lowerOrEqual
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.MainCoordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Creates a number of mountain tops and slowly decreases the terrain around them.
 */
class MountainTopHeightMapper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {
    private val MAX_TERRAIN = if (context.hilliness < 0.3) TerrainHeight.MAX - 1 else TerrainHeight.MAX
    private val MIN_TERRAIN = TerrainHeight.FOUR

    private val log = LoggerFactory.getLogger(this::class)

    private val mountainTopsPerMainCoordinate: Int = (4.0 * context.hilliness).roundToInt()

    /**
     * x % of main coordinates are picked to put a mountain in them
     */
    private val mainCoordinateQuotaForMountains = 0.5 * context.hilliness
    private val steepness = 0.55

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        // pick random positions for mountain tops
        val mountainTopLocations = findMountainTops(area, mutableWorld)
        val frontier = mutableSetOf<Coordinate>()
        if (mountainTopLocations.isNotEmpty()) {
            log.debug("Generating mountains between height $MAX_TERRAIN and $MIN_TERRAIN")
            // set those to max height
            mountainTopLocations.forEach {
                mutableWorld[it] = MAX_TERRAIN
                frontier.add(it)
                context.mountainTops.addTarget(it)
            }
            // from each position find the four neighbours that have no height yet
            generateMountains(frontier, mutableWorld)
        }
        log.debug("Processed ${processedTiles.size} tiles to create ${mountainTopLocations.size} mountains")
    }

    private fun generateMountains(initialFrontier: Set<Coordinate>, mutableWorld: MutableWorld) {
        var targetDistance = 0
        workFrontier(
                initialFrontier,
                { currentCoordinate: Coordinate ->
                    val currentHeight: TerrainHeight? = mutableWorld.heightAt(currentCoordinate)
                    if (currentHeight != null && currentHeight > MIN_TERRAIN) {
                        mutableWorld.
                                neighborsOf(currentCoordinate).
                                filter { it.isNotProcessed() }.
                                forEach { neighbor ->
                                    mutableWorld[neighbor] = currentHeight.lowerOrEqual(rand, steepness)
                                    neighbor.addToNextFrontier()
                                    context.mountainTops.add(neighbor, currentCoordinate, targetDistance)
                                }
                    }
                },
                { targetDistance++ })
    }

    private fun findMountainTops(area: CoordinateArea, mutableWorld: MutableWorld): Set<Coordinate> {
        val mainCoordinates = mutableWorld.mainCoordinatesEmpty.
            ifEmpty {
                log.debug("No empty space found for mountains!")
                return emptySet()
            }
        val mountainTopAreasToFind = (mainCoordinates.size.toDouble() * mainCoordinateQuotaForMountains).toInt()
        log.debug("Trying to find $mountainTopAreasToFind areas for mountain tops in ${mainCoordinates.size} main coordinates")
        val pickedAreas = mutableSetOf<MainCoordinate>()
        var tries = 0
        while (pickedAreas.size < mountainTopAreasToFind && tries < 1000) {
            val candidate = mainCoordinates.random(rand)
            if (candidate.toCoordinate() in area) {
                pickedAreas.add(candidate)
            }
            tries++
        }

        val pickedLocations = mutableSetOf<Coordinate>()
        pickedAreas.forEach { mainCoordinate ->
            for (i in 0 until mountainTopsPerMainCoordinate) {
                val pickedLocation = pickRandomLocation(mainCoordinate, pickedLocations, mutableWorld)
                pickedLocations.add(pickedLocation)
            }
        }
        log.debug("Found ${pickedLocations.size} mountain top locations: ${pickedLocations.sorted()}")
        return pickedLocations.toSet()
    }

    /** @return a random location inside the given []mainCoordinate] that is also in the given world and tries not to be in [pickedLocations] */
    private fun pickRandomLocation(mainCoordinate: MainCoordinate, pickedLocations: MutableSet<Coordinate>, mutableWorld: MutableWorld): Coordinate {
        var pickedLocation: Coordinate
        var tries = 3
        do {
            val randomLocation = mainCoordinate.toCoordinate()
                    .movedBy(rand.nextInt(MainCoordinate.TILE_COUNT), rand.nextInt(MainCoordinate.TILE_COUNT))
            pickedLocation = mutableWorld.moveInside(randomLocation)
        } while (!pickedLocations.contains(pickedLocation) && --tries > 0)
        return pickedLocation
    }

}
