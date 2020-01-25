package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.builder.modifier.BorderBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols

/**
 * Serves all Tile that are needed in the game.
 */
object TileRepository {

    object Elements {

        val PLATOON_FRIENDLY: Tile =
                Tile.newBuilder().
                        withForegroundColor(ColorRepository.FRIENDLY).
                        withBackgroundColor(ColorRepository.FRIENDLY_TRANSPARENT).
                        withCharacter('X').
                        withModifiers(BorderBuilder.newBuilder().withBorderColor(ColorRepository.FRIENDLY).build()).
                        buildCharacterTile()

    }

    fun createTerrainTile(terrain: Terrain): Tile =
            Tile.
                newBuilder().
                withForegroundColor(ColorRepository.forType(terrain.type)).
                withBackgroundColor(ColorRepository.forHeight(terrain.height)).
                withCharacter(terrain.type.char()).
                buildCharacterTile()

    private fun TerrainType.char() = when (this) {
        TerrainType.GRASSLAND -> '\"'
        TerrainType.FOREST    -> Symbols.SPADES
        TerrainType.HILL      -> Symbols.INTERSECTION
        TerrainType.MOUNTAIN  -> Symbols.TRIANGLE_UP_POINTING_BLACK
    }

    fun empty() = Tile.empty()

    fun forSignal(signalStrength: Double): Tile {
        return Tile.
                newBuilder().
                withForegroundColor(ColorRepository.radioColor(signalStrength)).
                withBackgroundColor(ColorRepository.radioColor(signalStrength)).
                withCharacter(' ').
                buildCharacterTile()
    }
}
