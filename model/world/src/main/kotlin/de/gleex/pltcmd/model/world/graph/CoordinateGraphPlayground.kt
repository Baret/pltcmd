package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.debug.DebugFeature
import de.gleex.pltcmd.util.graph.visualization.GraphDisplayer
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {  }

@DebugFeature("Just to play around with coordinate graph and to display simple ones")
fun main() {
    val tileGenerator = { it: Coordinate -> WorldTile(it, Terrain.random(Random)) }
    val sectorCoordinates = CoordinateRectangle(Coordinate(100, 200), Sector.TILE_COUNT, Sector.TILE_COUNT)
        .toSortedSet()
    val coordinateGraph = CoordinateGraph.of(sectorCoordinates)
    val sectorTiles: Map<Coordinate, WorldTile> = coordinateGraph.coordinates.associateWith { tileGenerator(it) }
    val largeGraph = WorldMapGraph(coordinateGraph, sectorTiles)
    log.debug { "Displaying large graph with size ${coordinateGraph.size}" }
//    largeGraph.display()

    log.debug { "Deriving subgraph" }
    val subGraph = coordinateGraph.subGraphFor(CoordinateRectangle(Coordinate(112, 210), 3, 3))
    log.debug { "Displaying sub graph $subGraph" }
    subGraph.display()

    log.debug { "Deriving another subgraph that only partially overlaps" }
    val subGraph2 = coordinateGraph.subGraphFor(CoordinateRectangle(Coordinate(95, 195), 7, 7))
    log.debug { "Displaying sub graph $subGraph2" }
    subGraph2.display()

    val plusGraph = subGraph + subGraph2
    log.debug { "subgraph + subgraph2 = $plusGraph" }
    plusGraph.display()
}

private fun WorldMapGraph.display() {
    GraphDisplayer.displayGraph(
        graph = coordinateGraph.graph,
        vertexLabelProvider = { "${it}\n${this[it]?.terrain}" },
//        edgeLabelProvider = { "${it.source} - ${it.destination}" }
    )
}

private fun CoordinateGraph.display() {
    GraphDisplayer.displayGraph(
        graph = graph,
        vertexLabelProvider = { it.toString() },
//        edgeLabelProvider = { "${it.source} - ${it.destination}" }
    )
}

private fun CoordinateGraphView.display() {
    coordinates.coordinates.display()
}

