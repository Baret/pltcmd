package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.extensions.normalDistributed
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.areafinder.SizedEmptyRectangleAreaFinder
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Generates multiple plains in an area. Plains are rectangles of grass land with a height between two to five. They have a minimum height/width of at least 5 tiles and a maximum of 50.
 */
class PlainsGenerator(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    companion object {
        private val log = KotlinLogging.logger {}
        private const val MIN_WIDTH = 5  // tiles
        private const val MAX_WIDTH = 50 // tiles
        private const val FADING_BORDER = 3 // tiles
        private const val UNEVENNESS = 0.06 // probability
    }

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        val totalPlainsTiles = totalTiles(area)
        val plainsRectangles = findPlainsLocations(totalPlainsTiles, area, mutableWorld)
        log.debug("Generating ${plainsRectangles.size} plains with a maximum size of $totalPlainsTiles coordinates")
        plainsRectangles.forEach {
            generatePlains(it, mutableWorld)
        }
    }

    /** Return the number of tiles in the given area that should be plains */
    private fun totalTiles(area: CoordinateArea): Int {
        val plainsRatio = 1.0 - context.hilliness
        log.debug("Using $plainsRatio of the area for plains")
        return (area.size * plainsRatio).roundToInt()
    }

    /** Searches empty spaces in the given area that can be used for plains (must have the required size) */
    private fun findPlainsLocations(totalPlainsTiles: Int, area: CoordinateArea, mutableWorld: MutableWorld): Set<CoordinateRectangle> {
        // TODO respect area. But currently the area is the full world anyway o_O
        log.debug("Finding empty rectangles that span a total of $totalPlainsTiles coordinates")
        val start = System.currentTimeMillis()
        val emptyRectangles = SizedEmptyRectangleAreaFinder(MIN_WIDTH, MIN_WIDTH, MAX_WIDTH, MAX_WIDTH).findAll(mutableWorld)
        val duration = System.currentTimeMillis() - start
        log.debug("Took $duration ms to find ${emptyRectangles.size} empty rectangles")
        var sum = 0
        val result = emptyRectangles
                .shuffled(rand)
                .takeWhile { sum += it.size; sum < totalPlainsTiles }
                .toSet()
        log.debug("found a total of $sum tiles: $result")
        return result
    }

    /** Set the [Terrain] in the given rectangle. The edges will randomly fade out to void. */
    private fun generatePlains(emptyRectangle: CoordinateRectangle, mutableWorld: MutableWorld) {
        val plainsHeight = listOf(
                TerrainHeight.TWO,
                TerrainHeight.THREE,
                TerrainHeight.FOUR,
                TerrainHeight.FIVE)
                .random(rand)
        val plains = Terrain.of(TerrainType.GRASSLAND, plainsHeight)
        val heightPlus1 = plainsHeight + 1
        val heightMinus1 = plainsHeight - 1
        val unevennessHeightChance = UNEVENNESS / 4.0
        val unevennessForest = UNEVENNESS * 2.0 / 4.0
        val plainsArea = createPlainsArea(emptyRectangle)
        plainsArea.forEach { coordinate ->
            if (!fadedBorder(coordinate, plainsArea)) {
                mutableWorld[coordinate] = plains
                when (rand.nextDouble()) {
                    in 0.0..unevennessHeightChance         -> mutableWorld[coordinate] = heightMinus1
                    in 0.5..(0.5 + unevennessForest)       -> mutableWorld[coordinate] = TerrainType.FOREST
                    in (1.0 - unevennessHeightChance)..1.0 -> mutableWorld[coordinate] = heightPlus1
                }
            }
        }
    }

    /**
     * Checks if the given coordinate lies in the border of the given rectangle.
     * If so it determines randomly based on the distance to the edge if it should be empty (faded) or not.
     * @return true if the given coordinate should not belong to the plains in the rectangle
     **/
    private fun fadedBorder(coordinate: Coordinate, rectangle: CoordinateRectangle): Boolean {
        val distanceLeft = coordinate.eastingFromLeft - rectangle.bottomLeftCoordinate.eastingFromLeft
        val distanceBottom = coordinate.northingFromBottom - rectangle.bottomLeftCoordinate.northingFromBottom
        val distanceRight = rectangle.topRightCoordinate.eastingFromLeft - coordinate.eastingFromLeft
        val distanceTop = rectangle.topRightCoordinate.northingFromBottom - coordinate.northingFromBottom

        val fadeChance = when {
            distanceLeft <= FADING_BORDER   -> fade(distanceLeft)
            distanceBottom <= FADING_BORDER -> fade(distanceBottom)
            distanceRight <= FADING_BORDER  -> fade(distanceRight)
            distanceTop <= FADING_BORDER    -> fade(distanceTop)
            else                            -> 0.0
        }
        return rand.nextDouble() <= fadeChance
    }

    /** Returns the chance to fade (not generate something) based on the distance to a border. */
    private fun fade(distance: Int): Double {
        // more distance should have lower chance so invert it
        // +1 to have 100% for the tile beside the border
        return (FADING_BORDER - distance) / (FADING_BORDER + 1.0)
    }

    /**
     * Defines the part of the given empty rectangle which should be filled as plains.
     * A normal distributed random number between the minimum and given size will be used for each direction.
     * A centered rectangle with the determined size will be carved out from the given rectangle.
     **/
    private fun createPlainsArea(emptyRectangle: CoordinateRectangle): CoordinateRectangle {
        val plainsWidth = randomGauss(emptyRectangle.width)
        val plainsHeight = randomGauss(emptyRectangle.height)
        log.debug("Creating plains $plainsWidth x $plainsHeight in area ${emptyRectangle.width} x ${emptyRectangle.height} = $emptyRectangle")
        return centerInRectangle(emptyRectangle, plainsWidth, plainsHeight)
    }

    /** Returns a normally distributed random value between MIN_WIDTH and given maxValue */
    private fun randomGauss(maxValue: Int): Double {
        val halfRange = (maxValue - MIN_WIDTH) / 2.0
        // therefore we use the middle of the range as center for the random values
        val randomValue = rand.normalDistributed(MIN_WIDTH + halfRange, halfRange)
        return randomValue.coerceIn(MIN_WIDTH.toDouble(), maxValue.toDouble())
    }

    /** Creates a rectangle with the given size that is centered in the given rectangle */
    private fun centerInRectangle(emptyRectangle: CoordinateRectangle, plainsWidth: Double, plainsHeight: Double): CoordinateRectangle {
        val halfDeltaWidth = ((emptyRectangle.width - plainsWidth) / 2.0).toInt()
        val halfDeltaHeight = ((emptyRectangle.height - plainsHeight) / 2.0).toInt()
        val bottomLeft = emptyRectangle.bottomLeftCoordinate.movedBy(halfDeltaWidth, halfDeltaHeight)
        return CoordinateRectangle(bottomLeft, plainsWidth.toInt(), plainsHeight.toInt())
    }

}