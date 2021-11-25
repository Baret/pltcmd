package de.gleex.pltcmd.model.world.graph

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
    val verticesLarge = CoordinateRectangle(Coordinate(10, 10), 10, 10)
        .map { TileVertex(WorldTile(it, Terrain.random(Random))) }
        .toSortedSet()
    val largeGraph = CoordinateGraph.of(verticesLarge)
    log.debug { "Displaying large graph with size ${largeGraph.size}" }
    val vertexLabelProvider: (TileVertex) -> String = { "${it.coordinate}\n${it.terrainType}\n${it.terrainHeight}" }
    GraphDisplayer.displayGraph(largeGraph.graph, vertexLabelProvider)

    log.debug { "Deriving subgraph" }
    val subGraph = largeGraph.subGraphFor(CoordinateRectangle(Coordinate(12, 12), 3, 3))
    log.debug { "Displaying sub graph with size ${subGraph.size}" }
    GraphDisplayer.displayGraph(subGraph.graph, vertexLabelProvider)

    log.debug { "Deriving another subgraph" }
    val subGraph2 = largeGraph.subGraphFor(CoordinateRectangle(Coordinate(5, 5), 7, 7))
    log.debug { "Displaying sub graph with size ${subGraph2.size}" }
    GraphDisplayer.displayGraph(subGraph2.graph, vertexLabelProvider)
}