package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.GameWorld
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics
import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.internal.modifier.TileCoordinate
import de.gleex.pltcmd.game.TileRepository
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.TileColors
import de.gleex.pltcmd.game.ColorRepository

/** Draws a grid around the [GameWorld] that shows the [Coordinate]s of the visible area like a border or box. */
class MapGridDecorationRenderer(val world: GameWorld) : ComponentDecorationRenderer {

	// top, left, bottom, right
	override val occupiedSize = Size.create(2, 2)
	override val offset = Position.create(1, 1)

	override fun render(tileGraphics: SubTileGraphics, context: ComponentDecorationRenderContext) {
		// TODO move drawing code into a graphics class
		// much copied from DefaulBox.init
		val size = tileGraphics.size
		val topLeftPos = size.fetchTopLeftPosition()
		val topRightPos = size.fetchTopRightPosition()
		val bottomLeftPos = size.fetchBottomLeftPosition()
		val bottomRightPos = size.fetchBottomRightPosition()

		// corners
		tileGraphics.setTileAt(topLeftPos, createTile(Symbols.SINGLE_LINE_TOP_LEFT_CORNER))
		tileGraphics.setTileAt(topRightPos, createTile(Symbols.SINGLE_LINE_TOP_RIGHT_CORNER))
		tileGraphics.setTileAt(bottomLeftPos, createTile(Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER))
		tileGraphics.setTileAt(bottomRightPos, createTile(Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER))
		// edges
		val horizontalLine = LineFactory.buildLine(Position.create(0, 0), Position.create(size.width - 3, 0))
			.toTileGraphics(createTile(Symbols.SINGLE_LINE_HORIZONTAL), tileGraphics.currentTileset())
		horizontalLine.drawOnto(tileGraphics, topLeftPos + Position.create(1, 0))
		horizontalLine.drawOnto(tileGraphics, topLeftPos + Position.create(1, size.height - 1))
		val verticalLine = LineFactory.buildLine(Position.create(0, 0), Position.create(0, size.height - 3))
			.toTileGraphics(createTile(Symbols.SINGLE_LINE_VERTICAL), tileGraphics.currentTileset())
		verticalLine.drawOnto(tileGraphics, topLeftPos + Position.create(0, 1))
		verticalLine.drawOnto(tileGraphics, topLeftPos + Position.create(size.width - 1, 1))
		val majorGridMarker =
			TileGraphicsBuilder.newBuilder().withSize(Size.create(1, 1))
				.withTile(Position.create(0, 0), createTile(Symbols.SINGLE_LINE_CROSS).withForegroundColor(ColorRepository.GRID_COLOR_HIGHLIGHT))
				.build()
		val minorGridMarker =
			TileGraphicsBuilder.newBuilder().withSize(Size.create(1, 1))
				.withTile(Position.create(0, 0), createTile(Symbols.SINGLE_LINE_CROSS))
				.build()
		// grid markers (for square)
		for (i in 1 until size.width step 5) {
			val gridMarker = if ((i - 1) % 10 == 0) {
				majorGridMarker
			} else {
				minorGridMarker
			}
			// top
			gridMarker.drawOnto(tileGraphics, topLeftPos.withRelativeX(i))
			// left
			gridMarker.drawOnto(tileGraphics, topLeftPos.withRelativeY(i))
			// bottom
			gridMarker.drawOnto(tileGraphics, bottomLeftPos.withRelativeX(i))
			// right
			gridMarker.drawOnto(tileGraphics, topRightPos.withRelativeY(i))
		}
	}

	private fun createTile(character: Char): Tile {
		return Tiles.newBuilder().withForegroundColor(ColorRepository.GRID_COLOR)
			.withBackgroundColor(TileColor.transparent()).withCharacter(character).buildCharacterTile()
	}
}