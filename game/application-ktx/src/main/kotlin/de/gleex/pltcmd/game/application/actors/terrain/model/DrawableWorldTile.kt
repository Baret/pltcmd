package de.gleex.pltcmd.game.application.actors.terrain.model

import de.gleex.pltcmd.model.world.WorldTile

/**
 * Holds a [WorldTile] and the information which of its neighbors have the same height and which have the same
 * terrain type.
 */
data class DrawableWorldTile(
    val tile: WorldTile,
    val neighborsSameHeight: NeighborBitmap,
    val neighborsSameTerrain: NeighborBitmap
)
