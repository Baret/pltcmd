package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

class MapBlock(terrain: Terrain): BlockBase<Tile>() {

    companion object {
        const val layersPerBlock = 3
    }

    private var unitTile: Tile = TileRepository.empty()
    private val terrainTile: Tile = TileRepository.forType(terrain.type)
    private val heightTile: Tile = TileRepository.forHeight(terrain.height)

    override val layers: MutableList<Tile>
        get() = mutableListOf(heightTile, terrainTile, unitTile)

    override fun fetchSide(side: BlockSide) = TileRepository.empty()
}