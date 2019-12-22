package de.gleex.pltcmd

import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.graphics.Symbols

const val MAP_VIEW_WDTH = 53
const val MAP_VIEW_HEIGHT = 53
const val INTERFACE_PANEL_WIDTH = 16
const val LOG_AREA_HEIGHT = 10

fun main() {
    val grid = SwingApplications.startTileGrid(
            AppConfigs.newConfig().
                    withSize(Sizes.create(MAP_VIEW_WDTH + INTERFACE_PANEL_WIDTH, MAP_VIEW_HEIGHT + LOG_AREA_HEIGHT)).
                    withDefaultTileset(CP437TilesetResources.rexPaint16x16()).
                    //fullScreen().
                    build())
    println("started with size ${grid.size}")

    val mainScreen = Screens.createScreenFor(grid)

    val redBG = TileColor.create(200,20,20, 255)
    val redTile = Tiles.newBuilder().
            withCharacter(Symbols.BLOCK_SOLID).
            withForegroundColor(redBG).
            withBackgroundColor(redBG).
            build()

    val controlsPanel = Components.
            panel().
            withSize(Sizes.create(INTERFACE_PANEL_WIDTH, mainScreen.height)).
            withPosition(Position.topLeftCorner()).
            wrapWithBox(true).
            withBoxType(BoxType.SINGLE).
            build()
    mainScreen.addComponent(controlsPanel)

    val logArea = Components.
            logArea().
            withSize(Sizes.create(grid.size.width - controlsPanel.size.width -2, LOG_AREA_HEIGHT-2)).
            withTitle("Radio log").
            wrapWithBox(true).
            withBoxType(BoxType.LEFT_RIGHT_DOUBLE).
            withPosition(controlsPanel.position.withRelativeX(controlsPanel.size.width).withRelativeY(MAP_VIEW_HEIGHT)).
            build()


    mainScreen.addComponent(logArea)

    println("size panel1 ${controlsPanel.size}, contentsize ${controlsPanel.contentSize}")
    println("size panel1 ${logArea.size}, contentsize ${logArea.contentSize}")

    mainScreen.display()
}