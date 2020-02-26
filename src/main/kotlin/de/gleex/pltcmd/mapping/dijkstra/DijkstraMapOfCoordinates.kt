package de.gleex.pltcmd.mapping.dijkstra

import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.logging.api.LoggerFactory

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
     * The highest value present in this map.
     */
    val maxDistance: Int = map.values.max() ?: 0

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
    }


    /**
     * Calculates the path from the given coordinate to a desired value (default 0) along this map.
     * If the coordinate is not present in this map an empty sequence is returned. If it already
     * has the given [targetValue] a sequence containing only this coordinate is returned.
     */
    fun pathFrom(from: Coordinate, targetValue: Int = 0): Sequence<Coordinate> {
        val startValue = map[from]
        return when {
            startValue == null        -> {
                emptySequence()
            }
            startValue == targetValue -> {
                sequenceOf(from)
            }
            startValue > targetValue  -> {
                downstream(from, targetValue)
            }
            else                      -> {
                upstream(from, targetValue)
            }
        }
    }

    private fun downstream(from: Coordinate, targetValue: Int) =
            internalPath(from, targetValue, true)

    private fun upstream(from: Coordinate, targetValue: Int) =
            internalPath(from, targetValue, false)

    private fun internalPath(from: Coordinate, targetValue: Int, downstream: Boolean): Sequence<Coordinate> {
        return sequence {
            var currentValue = map[from]
            var next: Coordinate? = from
            if(next != null || currentValue == targetValue) {
                yield(next!!)
                val allNeighbors = neighborsOf(next!!).
                        map { it to map[it] }.
                        filter{ it.second != null }
                val nextNeighbor = if(downstream) {
                            allNeighbors.minBy { it.second!! }
                        } else {
                            allNeighbors.maxBy { it.second!! }
                        }
                next = nextNeighbor?.
                        also { currentValue = it.second!! }?.
                        first
            }
        }
    }

    private fun neighborsOf(coordinate: Coordinate) = coordinate.neighbors()
}