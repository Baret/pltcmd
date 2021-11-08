package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.WorldTile

/**
 * A [CoordinateVertex] pointing at a [WorldTile] and thus effectively representing a terrain at this coordinate.
 */
open class TileVertex(val tile: WorldTile): CoordinateVertex(tile.coordinate) {
    val terrainType = tile.terrain.type
    val terrainHeight = tile.terrain.height
}
