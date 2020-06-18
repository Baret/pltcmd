package de.gleex.pltcmd.game.ui.composites

import de.gleex.pltcmd.game.options.UiOptions
import javafx.scene.image.Image
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ImageDictionaryTilesetResources
import org.hexworks.zircon.api.Modifiers
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.shape.LineFactory
import java.io.File
import kotlin.math.roundToInt

/**
 * The logo used as splash screen. Intended to be drawn onto the full screen, but might also be used in other places
 */
object GameLogo {
    private val style = StyleSet.create(UiOptions.THEME.primaryForegroundColor, ANSITileColor.BLACK)
    private val styleFadingShadow = style
            .withModifiers(Modifiers.fadeIn(20, 2000, true))
            .withForegroundColor(UiOptions.THEME.primaryBackgroundColor)
            .withBackgroundColor(ANSITileColor.BLACK)

    private val log = LoggerFactory.getLogger(GameLogo.javaClass)

    fun drawOnto(parent: TileGrid) {
        val logoSize = Size.create(parent.width, (parent.width / 1.6).roundToInt())
        val logoOffset = Position.zero().withRelativeY(((parent.height - logoSize.height) / 2.0).roundToInt())

        val mainLayer = LayerBuilder.newBuilder()
                .withFiller(Tile.createCharacterTile(' ', style))
                .withSize(parent.size)
                .build()

        parent.addLayer(mainLayer)
        val path = "./artwork/logos/"
        val tilesetResource = ImageDictionaryTilesetResources.loadTilesetFromFilesystem(path)
        val imageName = "logo_full_text_inv.png"
        val image = Image(File(path + imageName).inputStream())
        val tile = Tile.createImageTile(imageName, tilesetResource)
        log.info("Drawing tile $tile")
        val imageSizeInTiles = Size.create((image.width / mainLayer.tileset.width).toInt(), (image.height / mainLayer.tileset.height).toInt())
        val ox = (parent.size.width - imageSizeInTiles.width) / 2
        val oy = (parent.size.height - imageSizeInTiles.height) / 2
        val offSet = Position.create(ox, oy)
        log.info("image: ${image.width} * ${image.height} px, $imageSizeInTiles tiles")
        log.info("parent size: ${parent.size}")
        log.info("Drawing at offset $offSet")

        val imageLayer = LayerBuilder
                .newBuilder()
                .withOffset(offSet)
                .withSize(imageSizeInTiles)
                .build()
        imageLayer.draw(tile, imageLayer.position)
        parent.addLayer(imageLayer)
/*
        val logoCenterX = logoSize.width / 2
        val logoCenterY = logoSize.height / 2

        val distanceFromBorder = (logoSize.height * 0.31).roundToInt()

        val lowerPoint = Position.create(logoCenterX, logoSize.height - distanceFromBorder).withRelative(logoOffset)
        val upperPoint = Position.create(logoCenterX, distanceFromBorder).withRelative(logoOffset)
        val topLeftCorner = Position.zero().withRelative(logoOffset)
        val bottomRightCorner = Position.create(logoSize.width - 1, logoSize.height - 1).withRelative(logoOffset)

        mainLayer.drawMainLine(topLeftCorner, lowerPoint, upperPoint, bottomRightCorner)

        mainLayer.draw(
                CharacterTileStrings
                        .newBuilder()
                        .withText("p l t")
                        .build(),
                Position.create(logoCenterX - 6, logoCenterY).withRelative(logoOffset))

        mainLayer.draw(
                CharacterTileStrings
                        .newBuilder()
                        .withText("c m d")
                        .build(),
                Position.create(logoCenterX + 3, logoCenterY).withRelative(logoOffset))
 */
    }

    private fun Layer.drawMainLine(topLeftCorner: Position, lowerPoint: Position, upperPoint: Position, bottomRightCorner: Position) {
        val leftDiagonalLine = LineFactory.buildLine(topLeftCorner, lowerPoint)
        val rightDiagonalLine = LineFactory.buildLine(upperPoint, bottomRightCorner)
        val verticalLine = LineFactory.buildLine(upperPoint, lowerPoint)
        val diagonals = leftDiagonalLine.plus(rightDiagonalLine)
        (diagonals + verticalLine).positions.forEach {
            addBlockAt(it)
        }

        // add shadow above
        diagonals.positions.forEach {
            if (it.y > 0 && it.x != lowerPoint.x) {
                addShadowVertical(it)
            }
        }

        // add shadow right
        verticalLine.positions.forEach {
            if (it.y > upperPoint.y + 1) {
                addShadowHorizontal(it)
            }
        }
    }

    private fun Layer.addShadowVertical(pos: Position) = setShadow(pos.withRelativeY(-1), Symbols.LOWER_HALF_BLOCK)

    private fun Layer.addShadowHorizontal(pos: Position) = setShadow(pos.withRelativeX(1), Symbols.LEFT_HALF_BLOCK)

    private fun Layer.setShadow(position: Position, symbol: Char) = addTileAt(position, styleFadingShadow, symbol)

    private fun Layer.addBlockAt(pos: Position) = this.addTileAt(pos, style, Symbols.BLOCK_SOLID)

    private fun Layer.addTileAt(position: Position, style: StyleSet, symbol: Char) {
        draw(Tile.createCharacterTile(symbol, style), position)
    }
}
