package de.gleex.pltcmd.game.options

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.resource.TilesetResource

object UiOptions {
    fun buildAppConfig() =
            AppConfig.newBuilder().
                    // GameComponents is a beta feature
                    enableBetaFeatures().
                    withSize(WINDOW_WIDTH, WINDOW_HEIGHT).
                    withTitle("PltCmd").
                    withDefaultTileset(DEFAULT_TILESET).
                    withIcon("icon/applicationIcon.png").
                    build()

    val THEME = ColorThemes.cyberpunk()
    // Best tileset displaying pipes | either msGothic16x16() or bisasam16x16()
    val DEFAULT_TILESET: TilesetResource = CP437TilesetResources.bisasam16x16()
    val MAP_TILESET: TilesetResource = CP437TilesetResources.guybrush16x16()

    /**
     * The full width of the sidebars. The content size is probably this - 2.
     */
    const val SIDEBAR_WIDTH = 33

    const val MAP_VIEW_WIDTH = 53
    const val MAP_VIEW_HEIGHT = 53

    const val WINDOW_WIDTH = MAP_VIEW_WIDTH + (2 * SIDEBAR_WIDTH)
    const val WINDOW_HEIGHT = 64

    const val SKIP_INTRO = false
}