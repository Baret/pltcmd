package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

/**
 * A MapBlock represents one tile in the map view. It displays its immutable [Terrain]
 * and the unit (or its marker) at this tile.
 */
class MapBlock(val terrain: Terrain) : BaseBlock<Tile>(emptyTile = TileRepository.empty(), tiles = persistentMapOf()) {

    init {
        top = TileRepository.createTerrainTile(terrain)
    }

    /**
     * Sets the unit visible in this block
     */
    fun setUnit(unitTile: Tile) {
        content = unitTile
    }

    /**
     * Removes the currently visible unit in this block.
     */
    fun resetUnit() = setUnit(TileRepository.empty())

    fun hasUnit() = content != TileRepository.empty()
}