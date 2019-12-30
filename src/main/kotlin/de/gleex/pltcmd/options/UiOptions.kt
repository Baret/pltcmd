package de.gleex.pltcmd.options

import org.hexworks.zircon.api.AppConfigs

object UiOptions {
    fun buildAppConfig() =
            AppConfigs.
                    newConfig().
                    // GameComponents is a beta feature
                    enableBetaFeatures().
                    withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT).
                    withTitle("PltCmd").
                    build()

    const val WINDOW_WIDTH =  90
    const val WINDOW_HEIGHT = 63

    const val MAP_VIEW_WDTH = 53
    const val MAP_VIEW_HEIGHT = 53
    const val INTERFACE_PANEL_WIDTH = WINDOW_WIDTH - MAP_VIEW_WDTH
    const val INTERFACE_PANEL_HEIGHT = WINDOW_HEIGHT
    const val LOG_AREA_HEIGHT = WINDOW_HEIGHT - MAP_VIEW_HEIGHT
    const val LOG_AREA_WIDTH = WINDOW_WIDTH - INTERFACE_PANEL_WIDTH
}