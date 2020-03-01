package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.areafinder.SizedEmptyRectangleAreaFinder
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.CoordinateArea
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Generates multiple plains in an area. Plains are rectangles of grass land with a height between two to five. They have a minimum height/width of at least 5 tiles and a maximum of 50.
 */
class PlainsGenerator(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    companion object {
        private const val MIN_WIDTH = 5  // tiles
        private const val MAX_WIDTH = 50 // tiles
    }


    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        val totalPlainsTiles = totalTiles(area)
        val plainsRectangles = findPlainsLocations(totalPlainsTiles, area, mutableWorld)
        plainsRectangles.forEach {
            generatePlains(it, mutableWorld)
        }
    }

    private fun totalTiles(area: CoordinateArea): Int {
        return (area.size * context.plainsRatio).roundToInt()
    }

    private fun findPlainsLocations(totalPlainsTiles: Int, area: CoordinateArea, mutableWorld: MutableWorld): Set<CoordinateArea> {
        return SizedEmptyRectangleAreaFinder(MIN_WIDTH, MIN_WIDTH, MAX_WIDTH, MAX_WIDTH).findAll(mutableWorld)
    }

    private fun generatePlains(area: CoordinateArea, mutableWorld: MutableWorld) {
        val plainsHeight = listOf(
                TerrainHeight.TWO,
                TerrainHeight.THREE,
                TerrainHeight.FOUR,
                TerrainHeight.FIVE)
                .random(rand)
        val plains = Terrain.of(TerrainType.GRASSLAND, plainsHeight)
        area.forEach { coordinate ->
            mutableWorld[coordinate] = plains
        }
    }

}