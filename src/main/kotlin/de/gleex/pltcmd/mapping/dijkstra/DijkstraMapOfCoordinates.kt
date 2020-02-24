package de.gleex.pltcmd.mapping.dijkstra

import de.gleex.pltcmd.model.world.Coordinate

/**
 * not yet implemented!
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

    private val targets = map.filterValues { it == 0 }.keys

    fun pathFrom(from: Coordinate, targetValue: Int = 0): Sequence<Coordinate> {
        val startValue = map[from]
        return if(startValue == null) {
            emptySequence()
        } else if(startValue == targetValue) {
            return sequenceOf(from)
        } else if(startValue > targetValue) {
            downstream(from, targetValue)
        } else {
            emptySequence()
//            upstream(from, targetValue)
        }
    }

    private fun downstream(from: Coordinate, targetValue: Int): Sequence<Coordinate> {
        var next: Coordinate? = from
        return sequence {
            if(next != null) {
                yield(next!!)
                // TODO: find the neighbor with the lowest value in [map]
                next = neighborsOf(next!!).
                        filter { map.containsKey(it) }.
                        firstOrNull()
            }
        }
    }

    private fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors()
    }
}