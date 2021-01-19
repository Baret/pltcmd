package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.components.CustomComponent
import de.gleex.pltcmd.game.ui.components.ElementsTable
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

class ElementsDatabase(tileGrid: TileGrid) : BaseView(tileGrid, UiOptions.THEME) {
    init {
        screen.addComponent(CustomComponent(ElementsTable(screen.size), Position.zero()))
    }
}