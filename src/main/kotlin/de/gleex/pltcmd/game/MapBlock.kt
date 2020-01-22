package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

/**
 * A MapBlock represents one tile in the map view. It consists of multiple [layers], showing its immutable [Terrain]
 * and the unit (or its marker) at this tile.
 */
class MapBlock(terrain: Terrain) : BaseBlock<Tile>(Tile.empty(), persistentMapOf()) {

    private var unitTile: Tile = TileRepository.empty()
    private val terrainTile: Tile
    private val heightTile: Tile

    init {
        // TODO: Merge height and type tile into one
        terrainTile = TileRepository.forType(terrain.type)
        heightTile = TileRepository.forHeight(terrain.height)
        top = Tile.newBuilder().
                withForegroundColor(TileColor.create(42, 42, 255)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter('X').
                buildCharacterTile()
        bottom = TileRepository.createTerrainTile(terrain)
    }
}