package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.TileColors
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.graphics.Symbols

/**
 * Serves all Tiles that are needed in the game.
 */
object TileRepository {

    private val baseTerrainTile = Tiles.newBuilder().withBackgroundColor(TileColor.transparent())

    fun terrainTile(type: TerrainType) = when(type) {
        TerrainType.GRASSLAND -> baseTerrainTile.
                withCharacter('\"').
                withForegroundColor(TileColors.fromString("#05AA05")).
                buildCharacterTile()
        TerrainType.FOREST -> baseTerrainTile.
                withCharacter(Symbols.SPADES).
                withForegroundColor(ANSITileColor.GREEN).
                buildCharacterTile()
        TerrainType.HILL -> baseTerrainTile.
                withCharacter(Symbols.INTERSECTION).
                withForegroundColor(ANSITileColor.GRAY).
                buildCharacterTile()
        TerrainType.MOUNTAIN -> baseTerrainTile.
                withCharacter(Symbols.TRIANGLE_UP_POINTING_BLACK).
                withForegroundColor(TileColor.create(60, 60, 60, 255)).
                buildCharacterTile()
    }
}