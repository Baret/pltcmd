package de.gleex.pltcmd.game.ui.entities

import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.GraphicalTilesetResources
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

    private val tilesetResource = GraphicalTilesetResources.loadTilesetFromJar(16, 16, "/tileset_pltcmd.zip")
    private val log = LoggerFactory.getLogger(TileRepository.javaClass)

    init {
        log.info("loaded tileset $tilesetResource with path ${tilesetResource.path}")
        log.info("URI to zip: ${this.javaClass.getResource("/tileset_pltcmd.zip")}")
    }

    /**
     * All tiles used to display elements (aka units) on the map.
     */
    object Elements {
        private fun platoonTile(colorForeground: TileColor, colorBackground: TileColor, affiliationTag: String) =
                Tile.createGraphicTile("Infantry $affiliationTag", setOf(affiliationTag), tilesetResource)
//                newBuilder().
//                withTileset(tilesetResource).
//                withName("Infantry").
//                withTags(setOf(affiliationTag)).
//                withForegroundColor(colorForeground).
//                withBackgroundColor(colorBackground).
//                withCharacter('X').
//                withModifiers(BorderBuilder.newBuilder().withBorderColor(colorBackground).build()).
//                buildGraphicalTile()

        fun platoon(affiliation: Affiliation): Tile {
            // TODO: cache the tiles, because they are immutable (#101)
            val (foreground, background) = ColorRepository.forAffiliation(affiliation)
            val affiliationTag = affiliation.name.toLowerCase()
            log.info("Getting tile with affiliation $affiliationTag")
            return platoonTile(foreground, background, affiliationTag)
        }
    }

    fun createTerrainTile(terrain: Terrain): Tile = createTerrainTile(terrain.height, terrain.type)

    fun createTerrainTile(terrainHeight: TerrainHeight?, terrainType: TerrainType?): Tile =
            Tile.
                newBuilder().
                withForegroundColor(ColorRepository.forType(terrainType)).
                withBackgroundColor(ColorRepository.forHeight(terrainHeight)).
                withCharacter(terrainType.char()).
                buildCharacterTile()

    fun Tile.withGridBorder(borders: Set<BorderPosition>): Tile {
        return if (borders.isEmpty()) {
            this
        } else {
            withModifiers(BorderBuilder.newBuilder().
                    withBorderPositions(borders).
                    withBorderType(BorderType.DASHED).
                    withBorderColor(ColorRepository.GRID_COLOR).
                    build())
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
