package de.gleex.pltcmd.options

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.application.AppConfig

object UiOptions {
    fun buildAppConfig() =
            AppConfig.newBuilder().
                    // GameComponents is a beta feature
                    enableBetaFeatures().
                    withSize(WINDOW_WIDTH, WINDOW_HEIGHT).
                    withTitle("PltCmd").
                    withDefaultTileset(CP437TilesetResources.rexPaint16x16()).
                    build()

    val THEME = ColorThemes.default()

    const val WINDOW_WIDTH = 90
    const val WINDOW_HEIGHT = 63

    const val MAP_VIEW_WDTH = 53
    const val MAP_VIEW_HEIGHT = 53
    const val INTERFACE_PANEL_WIDTH = WINDOW_WIDTH - MAP_VIEW_WDTH
    const val INTERFACE_PANEL_HEIGHT = WINDOW_HEIGHT
    const val LOG_AREA_HEIGHT = WINDOW_HEIGHT - MAP_VIEW_HEIGHT
    const val LOG_AREA_WIDTH = WINDOW_WIDTH - INTERFACE_PANEL_WIDTH
}