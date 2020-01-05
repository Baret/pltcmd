package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.BlockSide
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BlockBase

class MapBlock(terrain: Terrain): BlockBase<Tile>() {

    private var unitTile: CharacterTile = Tiles.empty()
    private val heightTile: CharacterTile = Tiles.newBuilder().
            withBackgroundColor(ColorRepository.heihgtColor(terrain.height)).
            buildCharacterTile()
    private val terrainTile: CharacterTile = TileRepository.terrainTile(terrain.type)

    override val layers: MutableList<Tile>
        get() = mutableListOf(heightTile, terrainTile, unitTile)

    override fun fetchSide(side: BlockSide) = Tiles.empty()
}