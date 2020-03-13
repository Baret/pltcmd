package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.mapping.dijkstra.DijkstraMap
import de.gleex.pltcmd.model.world.Coordinate
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
    companion object {
        fun fromRandom(random: Random) = GenerationContext(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
    }

    val mountainTops = DijkstraMap<Coordinate>()
}
