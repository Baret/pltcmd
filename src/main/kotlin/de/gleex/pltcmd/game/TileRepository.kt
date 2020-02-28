package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.radio.SignalStrength
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import org.hexworks.zircon.api.builder.modifier.BorderBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.modifier.BorderType

/**
 * Serves all Tile that are needed in the game.
 */
object TileRepository {

    /**
     * All tiles used to display elements (aka units) on the map.
     */
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
        TerrainType.WATER_DEEP, TerrainType.WATER_SHALLOW -> Symbols.APPROXIMATION
    }

    fun empty() = Tile.empty()

    fun forSignal(signalStrength: SignalStrength): Tile {
        val signalColor = ColorRepository.radioColor(signalStrength)
        val tileBuilder = Tile.newBuilder()
                .withForegroundColor(signalColor)
                .withBackgroundColor(signalColor)
                .withCharacter(' ')

        when {
            signalStrength.isNone() -> {
                tileBuilder.
                        withCharacter(Symbols.SINGLE_LINE_CROSS).
                        withModifiers(
                            BorderBuilder.newBuilder().
                                    withBorderType(BorderType.SOLID).
                                    withBorderWidth(2).
                                    withBorderColor(ColorRepository.SIGNAL_EMPTY).
                                    build())
            }
            signalStrength.isFull() -> {
                tileBuilder.
                        withModifiers(
                            BorderBuilder.newBuilder().
                                    withBorderType(BorderType.SOLID).
                                    withBorderWidth(2).
                                    withBorderColor(ColorRepository.SIGNAL_FULL_HIGHLIGHT).
                                    build())
            }
        }

        return tileBuilder.buildCharacterTile()
    }
}
