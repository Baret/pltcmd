package de.gleex.pltcmd.ui

import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

class LoadingView(val tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    override fun onDock() {
        val panel = Components.panel()
                .withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT)
                .build()
        addTitle(panel)
        addLoading(panel)
        screen.addComponent(panel)
    }

    private fun addTitle(panel: Panel) {
        val title = Components.header()
                .withText("p l t c m d")
                .withAlignmentWithin(panel, ComponentAlignment.CENTER)
                .build()
        panel.addComponent(title)
    }

    private fun addLoading(panel: Panel) {
        val loading = Components.label()
                .withText("Generating world...")
                .withAlignmentWithin(panel, ComponentAlignment.BOTTOM_CENTER)
                .build()
        panel.addComponent(loading)
    }

}