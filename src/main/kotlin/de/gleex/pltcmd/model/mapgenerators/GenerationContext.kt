package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.mapping.dijkstra.DijkstraMap
import de.gleex.pltcmd.model.world.Coordinate
import kotlin.math.abs
import kotlin.random.Random

/**
 * A context for generating an area of the map. Contains values from 0 to 1 (exclusive) for different attributes for the map.
 */
data class GenerationContext(
        val vegetation: Double,
        val hilliness: Double,
        val water: Double,
        val urban: Double,
        /** open world to glue specific parts together */
        val undefined: Double
) {
    companion object {
        fun fromRandom(random: Random): GenerationContext {
            return GenerationContext(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(0.3, 1.0))
        }
    }

    val vegetationRatio: Double = asRatio(vegetation)
    val hillinessRatio: Double = asRatio(hilliness)
    val waterRatio: Double = asRatio(water)
    val urbanRatio: Double = asRatio(urban)
    val undefinedRatio: Double = asRatio(undefined)

    val mountainTops = DijkstraMap<Coordinate>()

    private fun asRatio(value: Double): Double {
        // use absolute value to count negative parts
        val sum = abs(vegetation) + abs(hilliness) + abs(water) + abs(urban) + abs(undefined)
        if (sum == 0.0) {
            return 0.0
        }
        return abs(value) / sum
    }

    override fun toString(): String {
        return "GenerationContext: %.1f %% vegetation, %.1f %% hilliness, %.1f %% water, %.1f %% urban, %.1f %% undefined, ".format(
                vegetationRatio * 100,
                hillinessRatio * 100,
                waterRatio * 100,
                urbanRatio * 100,
                undefinedRatio * 100
        )
    }

}
