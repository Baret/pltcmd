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
        fun fromRandom(random: Random): GenerationContext {
            val mapUnevenness = random.nextDouble()
            return GenerationContext(1.0 - mapUnevenness, random.nextDouble(), mapUnevenness, random.nextDouble(), random.nextDouble(), random.nextDouble(0.3, 1.0))
        }
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

    override fun toString(): String {
        return "GenerationContext: %.1f %% plains, %.1f %% forest, %.1f %% mountain, %.1f %% water, %.1f %% urban, %.1f %% undefined, ".format(
                plainsRatio * 100,
                forestRatio * 100,
                mountainRatio * 100,
                waterRatio * 100,
                urbanRatio * 100,
                undefinedRatio * 100
        )
    }

}
