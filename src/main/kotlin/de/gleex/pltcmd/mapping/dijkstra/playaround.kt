package de.gleex.pltcmd.mapping.dijkstra

import de.gleex.pltcmd.model.world.Coordinate

fun main() {

    val minimalMapValues = mapOf(
            Coordinate(0,0) to 2,
            Coordinate(1,0) to 1,
            Coordinate(2,0) to 2,
            Coordinate(0, 1) to 1,
            Coordinate(1, 1) to 0,
            Coordinate(2, 1) to 1,
            Coordinate(0, 2) to 2,
            Coordinate(1, 2) to 1,
            Coordinate(2, 2) to 2
    )

    val minimalMap = DijkstraMapOfCoordinates(minimalMapValues)

    println("Testing stuff...")
    val pathFrom = minimalMap.pathFrom(Coordinate(2, 2))
    pathFrom.forEachIndexed { index, value ->
        println("\ttake $index: $value")
    }
    println("Done with stuff!")
    println("")
    println("full path: ${pathFrom.toList()}")
}