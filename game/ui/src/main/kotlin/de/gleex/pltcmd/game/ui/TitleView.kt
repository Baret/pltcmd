package de.gleex.pltcmd.game.ui

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.ui.composites.GameLogo
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView
import kotlin.math.roundToInt

/** Displays the title of the game. */
class TitleView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val size = Size.create(tileGrid.width, (tileGrid.width / 1.6).roundToInt())

    init {
        println("creating gamelogo with size $size to tilegrid with size ${tileGrid.size}")
        screen.addFragment(GameLogo(size, screen))
    }
}