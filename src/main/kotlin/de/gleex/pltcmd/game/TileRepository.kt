package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols

/**
 * Serves all Tiles that are needed in the game.
 */
object TileRepository {

    private val heightTiles: MutableMap<TerrainHeight, Tile> = mutableMapOf()
    private val terrainTiles: MutableMap<TerrainType, Tile> = mutableMapOf()

    init {
        TerrainHeight.values().forEach { heightTiles[it] = initHeightTile(it) }
        TerrainType.values().forEach { terrainTiles[it] = initTerrainTile(it) }
    }

    /**
     * Returns the tile for the given [TerrainHeight]
     */
    fun forHeight(height: TerrainHeight) = heightTiles[height]!!

    /**
     * Returns the tile for the given [TerrainType]
     */
    fun forType(type: TerrainType) = terrainTiles[type]!!

    private fun initTerrainTile(type: TerrainType) = when(type) {
        TerrainType.GRASSLAND -> Tiles.newBuilder().
                withForegroundColor(ColorRepository.forType(type)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter('\"').
                build()
        TerrainType.FOREST -> Tiles.newBuilder().
                withForegroundColor(ColorRepository.forType(type)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(Symbols.SPADES).
                buildCharacterTile()
        TerrainType.HILL -> Tiles.newBuilder().
                withForegroundColor(ColorRepository.forType(type)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(Symbols.INTERSECTION).
                buildCharacterTile()
        TerrainType.MOUNTAIN -> Tiles.newBuilder().
                withForegroundColor(ColorRepository.forType(type)).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(Symbols.TRIANGLE_UP_POINTING_BLACK).
                buildCharacterTile()
    }

    private fun initHeightTile(height: TerrainHeight) = Tiles.newBuilder().
            withForegroundColor(TileColor.defaultForegroundColor()).
            withBackgroundColor(ColorRepository.forHeight(height)).
            withCharacter(Symbols.BLOCK_SOLID).
            buildCharacterTile()

    fun empty() = Tiles.empty()
}
