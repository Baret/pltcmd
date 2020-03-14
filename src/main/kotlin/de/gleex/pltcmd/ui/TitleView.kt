package de.gleex.pltcmd.ui

import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/** Displays the title of the game. */
class TitleView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    override fun onDock() {
        val panel = Components.panel()
                .withSize(screen.width, screen.height)
                .build()
        addTitle(panel)
        screen.addComponent(panel)
    }

    private fun addTitle(panel: Panel) {
        val title = Components.header()
                .withText("p l t c m d")
                .withAlignmentWithin(panel, ComponentAlignment.CENTER)
                .build()
        panel.addComponent(title)
    }

}