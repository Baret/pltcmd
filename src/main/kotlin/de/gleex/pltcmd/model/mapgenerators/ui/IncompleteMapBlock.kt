package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

/** Shows an optional terrain. Either [TerrainHeight] or [TerrainType] can be set or else will show a placeholder. */
class IncompleteMapBlock(terrainHeight: TerrainHeight?, terrainType: TerrainType?) : BaseBlock<Tile>(emptyTile = TileRepository.empty(), tiles = persistentMapOf()) {

    init {
        top = TileRepository.createTerrainTile(terrainHeight, terrainType)
    }

}
