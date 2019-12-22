package de.gleex.pltcmd

import org.hexworks.zircon.api.*

const val MAP_VIEW_WDTH = 53
const val MAP_VIEW_HEIGHT = 53
const val INTERFACE_PANEL_WIDTH = 16
const val LOG_AREA_HEIGHT = 12

fun main() {
    val grid = SwingApplications.startTileGrid(
            AppConfigs.newConfig().
                    withSize(Sizes.create(MAP_VIEW_WDTH + INTERFACE_PANEL_WIDTH, MAP_VIEW_HEIGHT + LOG_AREA_HEIGHT)).
                    withDefaultTileset(CP437TilesetResources.rexPaint16x16()).
                    build())
    println("started with size ${grid.size}")

    val mainScreen = Screens.createScreenFor(grid)

    mainScreen.addComponent(Components.panel().withSize(Sizes.create(2, 2)))
}