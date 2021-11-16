package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.WorldTile

/**
 * A graph built of [TileVertex]. It associates a terrain with each coordinate.
 */
open class TerrainGraph<V: TileVertex> private constructor(): CoordinateGraph<V>() {
    companion object {
        /**
         * Creates a [TerrainGraph] consisting of the given [WorldTile]s.
         *
         * @param tiles the tiles to be contained in this graph
         * @param tileTransform the mapping function to turn each [WorldTile] into a vertex object.
         */
        fun <V : TileVertex> of(tiles: Collection<WorldTile>, tileTransform: (WorldTile) -> V): TerrainGraph<V> {
            return TerrainGraph<V>().apply {
                tiles.map(tileTransform)
                    .forEach { addVertex(it) }
            }

        }
    }
}