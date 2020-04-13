package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.mapgeneration.dijkstra.DijkstraMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlin.random.Random

/**
 * A context for generating an area of the map. Contains values from 0 to 1 (exclusive) for different attributes for the map.
 */
data class GenerationContext(
        val vegetation: Double,
        val hilliness: Double,
        val water: Double,
        val urban: Double
) {
    init {
        require(0.0 <= vegetation && vegetation < 1.0)
        require(0.0 <= hilliness && hilliness < 1.0)
        require(0.0 <= water && water < 1.0)
        require(0.0 <= urban && urban < 1.0)
    }

    companion object {
        fun fromRandom(random: Random): GenerationContext {
            return GenerationContext(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
        }
    }

    val mountainTops = DijkstraMap<Coordinate>()

    override fun toString(): String {
        return "GenerationContext: %.1f %% vegetation, %.1f %% hilliness, %.1f %% water, %.1f %% urban".format(
                vegetation * 100,
                hilliness * 100,
                water * 100,
                urban * 100
        )
    }

}
