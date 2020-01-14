package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

/**
 * A MapBlock represents one tile in the map view. It consists of multiple [layers], showing its immutable [Terrain]
 * and the unit (or its marker) at this tile.
 */
class MapBlock(val terrain: Terrain): BlockBase<Tile>() {

    companion object {
        /**
         * The numbers of layers returned by [layers]
         */
        const val LAYERS_PER_BLOCK = 3
    }

    private var unitTile: Tile = TileRepository.empty()
    private val terrainTile: Tile = TileRepository.forType(terrain.type)
    private val heightTile: Tile = TileRepository.forHeight(terrain.height)

    /**
     * The layers of this block, which are from bottom to top: terrain height, terrain type, unit.
     */
    override val layers: MutableList<Tile>
        get() = mutableListOf(heightTile, terrainTile, unitTile)

    fun setUnit(tile: Tile) {
        unitTile = tile
    }

    override fun fetchSide(side: BlockSide) = TileRepository.empty()
}