package de.gleex.pltcmd.game.ui

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.composites.GameLogo
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/** Displays the title of the game. */
class TitleView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    init {
        GameLogo.drawOnto(screen)
    }
}