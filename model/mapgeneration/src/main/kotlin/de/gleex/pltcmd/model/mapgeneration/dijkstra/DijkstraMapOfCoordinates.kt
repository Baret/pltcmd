package de.gleex.pltcmd.model.mapgeneration.dijkstra

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.max
import kotlin.math.min

/**
 * not yet implemented!
 * TODO: Fix documentation
 */
class DijkstraMapOfCoordinates(private val map: Map<Coordinate, Int>) {
    // This should become a better implementation of a dijkstra map
    // It would get a set of coordinates, where you either put a number of "targets" (value 0)
    // and it generates the values of all other tiles.
    // Or you give it a function to calculate the value for every tile.
    // This way we would have a more robust dijkstra map that might also be used later in the game
    // instead of only in map generation.
    // It should be possible to abstract this implementation to also use zircon positions instead of coordinates
    // because you never know... :)

    private val log = LoggerFactory.getLogger(this::class)

    private val lowestValue = map.values.min() ?: 0

    /**
     * The highest distance to the lowest value ("target") in this map.
     *
     * This means the longest path is this + 1
     */
    val maxDistance: Int = map.values.max()?.minus(lowestValue) ?: 0

    /**
     * Targets are all fields with the lowest value in the map (not necessarily 0, negative values are possible).
     */
    val targets = map.filterValues { it == lowestValue }.keys

    init {
        require(map.isNotEmpty()) {
            "Can not create a dijkstra map without any entries! Given map is empty."
        }

        if(lowestValue > 0) {
            log.warn("Created dijkstra map with lowest value $lowestValue. Targets usually have value 0 (or lower).")
        }

        log.debug("Created dijkstra map with ${map.size} entries and ${targets.size} targets with value $lowestValue. Max Distance: $maxDistance")
    }


    /**
     * Calculates the path from the given coordinate to a desired value (default 0) along this map.
     * If the coordinate is not present in this map an empty sequence is returned. If it already
     * has the given [targetValue] a sequence containing only this coordinate is returned.
     */
    fun pathFrom(from: Coordinate, targetValue: Int = lowestValue): Sequence<Coordinate> {
        val startValue = map[from]
        return when {
            startValue == null        -> {
                emptySequence()
            }
            startValue == targetValue -> {
                sequenceOf(from)
            }
            startValue > targetValue  -> {
                downstream(from, max(targetValue, lowestValue))
            }
            else                      -> {
                upstream(from, min(targetValue, maxDistance))
            }
        }
    }

    private fun downstream(from: Coordinate, targetValue: Int) =
            generateSequence(from) { previous ->
                if(map[previous] == targetValue) {
                    null
                } else {
                    neighborsOf(previous).lowest(targetValue)
                }
            }

    private fun upstream(from: Coordinate, targetValue: Int) =
            generateSequence(from) { previous ->
                if(map[previous] == targetValue) {
                    null
                } else {
                    neighborsOf(previous).highest(targetValue)
                }
            }

    private fun neighborsOf(coordinate: Coordinate) = coordinate.neighbors().filter { map.containsKey(it) }

    private fun Collection<Coordinate>.lowest(targetValue: Int): Coordinate? {
        val lowestEntry = map.entries.
                filter { it.key in this }
                .minBy { it.value }
        return if(lowestEntry?.value != null && lowestEntry.value >= targetValue) {
                lowestEntry.key
            } else {
                null
            }
    }

    private fun Collection<Coordinate>.highest(targetValue: Int): Coordinate? {
        val highestEntry = map.entries.
                filter { it.key in this }
                .maxBy { it.value }
        return if(highestEntry?.value != null && highestEntry.value <= targetValue) {
            highestEntry.key
        } else {
            null
        }
    }

}
