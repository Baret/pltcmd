package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.mapping.dijkstra.DijkstraMap
import de.gleex.pltcmd.model.world.Coordinate
import kotlin.math.abs
import kotlin.random.Random

/**
 * A context for generating an area of the map. Contains values from 0 to 1 (exclusive) for different attributes for the map.
 */
data class GenerationContext(
        val plains: Double,
        val forest: Double,
        val mountain: Double,
        val water: Double,
        val urban: Double,
        /** open world to glue specific parts together */
        val undefined: Double
) {
    companion object {
        fun fromRandom(random: Random) = GenerationContext(.35, random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(), .5)
    }

    val plainsRatio: Double = asRatio(plains)
    val forestRatio: Double = asRatio(forest)
    val mountainRatio: Double = asRatio(mountain)
    val waterRatio: Double = asRatio(water)
    val urbanRatio: Double = asRatio(urban)
    val undefinedRatio: Double = asRatio(undefined)

    val mountainTops = DijkstraMap<Coordinate>()

    private fun asRatio(value: Double): Double {
        // use absolute value to count negative parts
        val sum = abs(plains) + abs(forest) + abs(mountain) + abs(water) + abs(urban) + abs(undefined)
        if (sum == 0.0) {
            return 0.0
        }
        return abs(value) / sum
    }

}
