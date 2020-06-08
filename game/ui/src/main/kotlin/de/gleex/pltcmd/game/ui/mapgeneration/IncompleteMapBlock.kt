package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.entities.TileRepository.withGridBorder
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock
import org.hexworks.zircon.api.modifier.BorderPosition

/** Shows an optional terrain. Either [TerrainHeight] or [TerrainType] can be set or else will show a placeholder. */
class IncompleteMapBlock(
        private val borders: Set<BorderPosition>
) : BaseBlock<Tile>(emptyTile = TileRepository.empty(), tiles = persistentMapOf()) {

    init {
        setTerrain(null, null)
    }

    fun setTerrain(terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        top = TileRepository.createTerrainTile(terrainHeight, terrainType)
                .withGridBorder(borders)
    }
}
