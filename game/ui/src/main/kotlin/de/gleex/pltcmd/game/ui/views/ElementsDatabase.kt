package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.components.CustomComponent
import de.gleex.pltcmd.game.ui.components.ElementsTable
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/**
 * This view contains a table to see and compare all elements currently present in the game.
 */
class ElementsDatabase(tileGrid: TileGrid) : BaseView(tileGrid, UiOptions.THEME) {
    init {
        screen.addComponent(CustomComponent(ElementsTable(screen.size), Position.zero()))
    }
}