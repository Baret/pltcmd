package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.terrain.Terrain
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

/** Shows either the [Terrain] of a map or a place holder if nothing exists yet. */
class IncompleteMapBlock(val terrain: Terrain?) : BaseBlock<Tile>(emptyTile = TileRepository.empty(), tiles = persistentMapOf()) {

    init {
        top = TileRepository.createTerrainTile(terrain)
    }

}
