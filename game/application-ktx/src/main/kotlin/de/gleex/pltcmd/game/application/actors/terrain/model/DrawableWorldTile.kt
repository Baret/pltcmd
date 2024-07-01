package de.gleex.pltcmd.game.application.actors.terrain.model

import de.gleex.pltcmd.game.engine.attributes.memory.KnownTerrain
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain

/**
 * Holds a [WorldTile] and the information which of its neighbors have the same height and which have the same
 * terrain type.
 */
data class DrawableWorldTile(
    val tile: KnownTerrain,
    val neighborsLower: NeighborBitmap,
    val neighborsSameTerrain: NeighborBitmap
) {
    /**
     * The coordinate of the underlying [KnownTerrain].
     */
    val coordinate: Coordinate by tile::coordinate
    /**
     * The [Terrain] of the underlying [KnownTerrain] in case it is revealed.
     */
    val terrain: Terrain? by tile::terrain
}
