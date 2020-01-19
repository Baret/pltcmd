package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.shape.LineFactory
import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.zircon.api.CharacterTileStrings
import org.hexworks.zircon.api.builder.data.TileBuilder

/**
 * Draws a border around a rectangle with a grid indicator every five tiles. Every second tile has a highlight color to see the main coordinates.
 */
class MapCoordinates(
        world: GameWorld,
        size: Size,
        styleSet: StyleSet,
        tileset: TilesetResource,
        private val backend: TileGraphics = TileGraphicsBuilder.newBuilder()
                .withTileset(tileset)
                .withSize(size)
                .build())
    : TileGraphics by backend {

    init {
        setStyleFrom(styleSet)

        val topLeftCoordinate = world.visibleTopLeftCoordinate()
        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position) {
        // grid coordinates (for square)
        for (i in 1 until size.width step 5) {
            // top
            val topCoord = topLeftCoordinate.withRelativeEasting(i - 1)
            draw(CoordinateTileString(topCoord.eastingFromLeft), topLeftPos.withRelativeX(i + 1)) // +1 offset by other decoration to map view position
            // left
            val leftCoord = topLeftCoordinate.withRelativeNorthing(-i + 1)
            draw(VerticalCoordinateTileString(leftCoord.northingFromBottom), topLeftPos.withRelativeY(i + 1)) // +1 offset by other decoration to map view position
        }
    }

    override fun toString() = "MapCoordinates $backend"

}
