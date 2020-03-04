package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.areafinder.SizedEmptyRectangleAreaFinder
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.CoordinateArea
import de.gleex.pltcmd.model.world.CoordinateRectangle
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Generates multiple plains in an area. Plains are rectangles of grass land with a height between two to five. They have a minimum height/width of at least 5 tiles and a maximum of 50.
 */
class PlainsGenerator(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {
    private val utilRandom = java.util.Random(rand.nextLong())

    companion object {
        private val LOG = LoggerFactory.getLogger(PlainsGenerator::class)
        private const val MIN_WIDTH = 5  // tiles
        private const val MAX_WIDTH = 50 // tiles
    }

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        val totalPlainsTiles = totalTiles(area)
        val plainsRectangles = findPlainsLocations(totalPlainsTiles, area, mutableWorld)
        LOG.debug("Generating ${plainsRectangles.size} plains with a maximum size of $totalPlainsTiles coordinates")
        plainsRectangles.forEach {
            generatePlains(it, mutableWorld)
        }
    }

    private fun totalTiles(area: CoordinateArea): Int {
        val plainsRatio = context.plainsRatio
        LOG.debug("Using $plainsRatio of the area for plains")
        return (area.size * plainsRatio).roundToInt()
    }

    private fun findPlainsLocations(totalPlainsTiles: Int, area: CoordinateArea, mutableWorld: MutableWorld): Set<CoordinateRectangle> {
        // TODO respect area. But currently the area is the full world anyway o_O
        LOG.debug("Finding empty rectangles that span a total of $totalPlainsTiles coordinates")
        val start = System.currentTimeMillis()
        val emptyRectangles = SizedEmptyRectangleAreaFinder(MIN_WIDTH, MIN_WIDTH, MAX_WIDTH, MAX_WIDTH).findAll(mutableWorld)
        val duration = System.currentTimeMillis() - start
        LOG.debug("Took $duration ms to find ${emptyRectangles.size} empty rectangles")
        var sum = 0
        val result = emptyRectangles
                .shuffled(rand)
                .takeWhile { sum += it.size; sum < totalPlainsTiles }
                .toSet()
        LOG.warn("found a total of $sum tiles: $result")
        return result
    }

    private fun generatePlains(emptyRectangle: CoordinateRectangle, mutableWorld: MutableWorld) {
        val plainsHeight = listOf(
                TerrainHeight.TWO,
                TerrainHeight.THREE,
                TerrainHeight.FOUR,
                TerrainHeight.FIVE)
                .random(rand)
        val plains = Terrain.of(TerrainType.GRASSLAND, plainsHeight)
        val plainsArea = createPlainsArea(emptyRectangle)
        plainsArea.forEach { coordinate ->
            mutableWorld[coordinate] = plains
        }
    }

    private fun createPlainsArea(emptyRectangle: CoordinateRectangle): CoordinateRectangle {
        val plainsWidth = randomGauss(emptyRectangle.width)
        val plainsHeight = randomGauss(emptyRectangle.height)
        LOG.debug("Creating plains $plainsWidth x $plainsHeight in area ${emptyRectangle.width} x ${emptyRectangle.height} = $emptyRectangle")
        return centerInRectangle(emptyRectangle, plainsWidth, plainsHeight)
    }

    private fun centerInRectangle(emptyRectangle: CoordinateRectangle, plainsWidth: Double, plainsHeight: Double): CoordinateRectangle {
        val halfDeltaWidth = ((emptyRectangle.width - plainsWidth) / 2.0).toInt()
        val halfDeltaHeight = ((emptyRectangle.height - plainsHeight) / 2.0).toInt()
        val bottomLeft = emptyRectangle.bottomLeftCoordinate.withRelativeEasting(halfDeltaWidth)
                .withRelativeEasting(halfDeltaHeight)
        val topRight = emptyRectangle.topRightCoordinate.withRelativeEasting(-halfDeltaWidth)
                .withRelativeEasting(-halfDeltaHeight)
        return CoordinateRectangle(bottomLeft, topRight)
    }

    /** Returns a normally distributed random value between MIN_WIDTH and given maxValue */
    private fun randomGauss(maxValue: Int): Double {
        val halfRange = (maxValue - MIN_WIDTH) / 2.0
        // therefore we use the middle of the range as center for the random values
        val randomValue = utilRandom.nextGaussian() * halfRange + halfRange
        return min(max(MIN_WIDTH.toDouble(), randomValue), MAX_WIDTH.toDouble())
    }

}