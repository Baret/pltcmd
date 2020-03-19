package de.gleex.pltcmd.ui

import org.hexworks.zircon.api.animation.Animation
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.internal.animation.impl.DefaultAnimationFrame

/** Displays the title of the game. */
fun createTitleAnimation(tileGrid: TileGrid) : Animation = Animation.newBuilder()
        .addFrame(DefaultAnimationFrame(tileGrid.size, listOf(TitleLayer(StyleSet.defaultStyle())), 20)) // TODO use style
        .build()

class TitleLayer(style: StyleSet) : Layer by Layer.newBuilder()
        .withTileGraphics(TitleGraphics(style))
        .build()

class TitleGraphics(style: StyleSet) : TileGraphics by TileGraphicsBuilder.newBuilder()
        .withTiles(titleTiles(style))
        .build()

//        .withText("p l t c m d")
fun titleTiles(style: StyleSet): Map<Position, Tile> {
    val tiles = mutableMapOf<Position, Tile>()
    tiles[Position.create(0, 0)] = Tile.createCharacterTile('p', style)
    tiles[Position.create(2, 0)] = Tile.createCharacterTile('l', style)
    tiles[Position.create(4, 0)] = Tile.createCharacterTile('t', style)
    tiles[Position.create(6, 0)] = Tile.createCharacterTile('c', style)
    tiles[Position.create(8, 0)] = Tile.createCharacterTile('m', style)
    tiles[Position.create(10, 0)] = Tile.createCharacterTile('d', style)
    return tiles
}
