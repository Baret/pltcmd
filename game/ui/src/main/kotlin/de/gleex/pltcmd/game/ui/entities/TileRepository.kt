package de.gleex.pltcmd.game.ui.entities

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.zircon.api.builder.modifier.BorderBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.modifier.BorderPosition
import org.hexworks.zircon.api.modifier.BorderType

/**
 * Serves all Tile that are needed in the game.
 */
object TileRepository {

    /**
     * All tiles used to display elements (aka units) on the map.
     */
    object Elements {
        private val CommandingElement.character: Char
            get() = kind
                .toFrontendString(Format.ICON)
                .value[0]

        private fun platoonTile(representation: Char, colorForeground: TileColor, colorBackground: TileColor) =
                Tile.newBuilder()
                        .withForegroundColor(colorForeground)
                        .withBackgroundColor(colorBackground)
                        .withCharacter(representation)
                        .withModifiers(BorderBuilder.newBuilder()
                                .withBorderColor(colorBackground)
                                .build())
                        .buildCharacterTile()

        fun marker(element: CommandingElement, affiliation: Affiliation): Tile {
            // TODO: cache the tiles, because they are immutable (#101)
            val (foreground, background) = ColorRepository.forAffiliation(affiliation)
            // TODO: use element.rung instead of fixed platoon
            return platoonTile(element.character, foreground, background)
        }
    }

    fun createTerrainTile(terrain: Terrain): Tile = createTerrainTile(terrain.height, terrain.type)

    fun createTerrainTile(terrainHeight: TerrainHeight?, terrainType: TerrainType?): Tile =
            Tile.newBuilder()
                    .withForegroundColor(ColorRepository.forType(terrainType))
                    .withBackgroundColor(ColorRepository.forHeight(terrainHeight))
                    .withCharacter(terrainType.char())
                    .buildCharacterTile()

    /**
     * Creates a tile with a gradient from the given terrain height to the next lower terrain height.
     * This tile may be used as the front of blocks.
     */
    fun createTerrainSideTile(terrainHeight: TerrainHeight?): Tile {
        val lowerColor = ColorRepository.forHeight(terrainHeight?.minus(1))
        val higherColor = lowerColor
                .interpolateTo(ColorRepository.forHeight(terrainHeight))
                .getColorAtRatio(.5)
        return Tile
                .newBuilder()
                .withForegroundColor(lowerColor)
                .withBackgroundColor(higherColor)
                .withCharacter(Symbols.LOWER_HALF_BLOCK)
                .buildCharacterTile()
    }

    fun Tile.withGridBorder(borders: Set<BorderPosition>): Tile {
        return if (borders.isEmpty()) {
            this
        } else {
            withModifiers(BorderBuilder.newBuilder()
                    .withBorderPositions(borders)
                    .withBorderType(BorderType.DASHED)
                    .withBorderColor(ColorRepository.GRID_COLOR)
                    .build())
        }
    }

    private fun TerrainType?.char(): Char {
        if (this == null) return Symbols.INVERTED_QUESTION_MARK
        return when (this) {
            TerrainType.GRASSLAND                             -> '\"'
            TerrainType.FOREST                                -> Symbols.SPADES
            TerrainType.HILL                                  -> Symbols.INTERSECTION
            TerrainType.MOUNTAIN                              -> Symbols.TRIANGLE_UP_POINTING_BLACK
            TerrainType.WATER_DEEP, TerrainType.WATER_SHALLOW -> Symbols.APPROXIMATION
        }
    }

    fun empty() = Tile.empty()

    /**
     * Returns a [Tile] to display the given signal strength.
     */
    fun forSignal(signalStrength: SignalStrength): Tile {
        val signalColor = ColorRepository.radioColor(signalStrength)
        val tileBuilder = Tile.newBuilder()
                .withForegroundColor(signalColor)
                .withBackgroundColor(signalColor)
                .withCharacter(' ')

        when {
            signalStrength.isNone() -> {
                tileBuilder.withCharacter(Symbols.BLOCK_SPARSE)
                        .withModifiers(
                                BorderBuilder.newBuilder()
                                        .withBorderType(BorderType.SOLID)
                                        .withBorderWidth(2)
                                        .withBorderColor(ColorRepository.SIGNAL_EMPTY)
                                        .build())
            }
            signalStrength.isFull() -> {
                tileBuilder.withModifiers(
                        BorderBuilder.newBuilder()
                                .withBorderType(BorderType.SOLID)
                                .withBorderWidth(2)
                                .withBorderColor(ColorRepository.SIGNAL_FULL_HIGHLIGHT)
                                .build())
            }
        }

        return tileBuilder.buildCharacterTile()
    }

    /**
     * Creates a [Tile] representing a base/FOB.
     */
    fun createFobTile(affiliation: Affiliation): Tile {
        val (foreground, background) = ColorRepository.forAffiliation(affiliation)
        return Tile.newBuilder()
            .withForegroundColor(foreground)
            .withBackgroundColor(background)
            .withCharacter(Symbols.SOLAR_SYMBOL)
            .buildCharacterTile()
    }
}
