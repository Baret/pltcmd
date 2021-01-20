package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.components.ElementsTable
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/**
 * This view contains a table to see and compare all elements currently present in the game.
 */
class ElementsDatabase(tileGrid: TileGrid) : BaseView(tileGrid, UiOptions.THEME) {
    init {
        val decor = ComponentDecorations.shadow()
        val panelSize = (ElementsTable.MIN_SIZE + decor.occupiedSize).withHeight(screen.height)
        val tablePanel = Components
            .panel()
            .withDecorations(decor)
            .withSize(panelSize)
            .build()
        tablePanel.addFragment(ElementsTable(tablePanel.contentSize))

        val detailsPanelSize = screen.size.withWidth(screen.width - tablePanel.width)
        val detailsPanel = Components
            .panel()
            .withDecorations(ComponentDecorations.halfBlock())
            .withSize(detailsPanelSize)
            .build()

        screen.addComponent(Components
            .hbox()
            .withSize(screen.size)
            .build()
            .apply {
                addComponents(tablePanel, detailsPanel)
            })
    }
}