package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols

/**
 * Serves all Tile that are needed in the game.
 */
object TileRepository {

    private val heightTile: MutableMap<TerrainHeight, Tile> = mutableMapOf()
    private val terrainTile: MutableMap<TerrainType, Tile> = mutableMapOf()

    init {
        TerrainHeight.values()
                .forEach { heightTile[it] = initHeightTile(it) }
        TerrainType.values()
                .forEach { terrainTile[it] = initTerrainTile(it) }
    }

    /**
     * Returns the tile for the given [TerrainHeight]
     */
    fun forHeight(height: TerrainHeight) = heightTile[height]!!

    /**
     * Returns the tile for the given [TerrainType]
     */
    fun forType(type: TerrainType) = terrainTile[type]!!

    private fun initTerrainTile(type: TerrainType) = when (type) {
        TerrainType.GRASSLAND -> characterTile(type, '\"')
        TerrainType.FOREST    -> characterTile(type, Symbols.SPADES)
        TerrainType.HILL      -> characterTile(type, Symbols.INTERSECTION)
        TerrainType.MOUNTAIN  -> characterTile(type, Symbols.TRIANGLE_UP_POINTING_BLACK)
    }

    private fun characterTile(type: TerrainType, character: Char) =
            Tile.
                newBuilder().
                withForegroundColor(ColorRepository.forType(type)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(character).
                buildCharacterTile()

    private fun initHeightTile(height: TerrainHeight) =
            Tile.
                newBuilder().
                withForegroundColor(ColorRepository.forHeight(height)).
                withBackgroundColor(ColorRepository.forHeight(height)).
                withCharacter(Symbols.BLOCK_SOLID).
                buildCharacterTile()

    fun empty() = Tile.empty()
}
