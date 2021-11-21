package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.WorldTile
import mu.KotlinLogging
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {  }

/**
 * A graph built of [TileVertex]. It associates a terrain with each coordinate.
 */
open class TerrainGraph<V: TileVertex> private constructor(vertices: SortedSet<V>): CoordinateGraph<V>(vertices) {

    companion object {
        /**
         * Creates a [TerrainGraph] consisting of the given [WorldTile]s.
         *
         * @param tiles the tiles to be contained in this graph
         * @param tileTransform the mapping function to turn each [WorldTile] into a vertex object.
         */
        @OptIn(ExperimentalTime::class)
        fun <V : TileVertex> of(tiles: SortedSet<WorldTile>, tileTransform: (WorldTile) -> V): TerrainGraph<V> {
            log.info { "Creating terrain graph with ${tiles.size} tiles" }
            val (graph, duration) = measureTimedValue {
                TerrainGraph(tiles.map(tileTransform).toSortedSet())
//                TerrainGraph<V>().apply {
//                    tiles.map(tileTransform)
//                        .forEach { addVertex(it) }
//                }
            }
            log.info { "Creation of terrain graph took $duration, avg per tile: ${duration / tiles.size}" }
            return graph
        }
    }
}