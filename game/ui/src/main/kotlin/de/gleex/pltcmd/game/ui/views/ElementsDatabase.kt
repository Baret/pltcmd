package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.model.elements.Elements
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

class ElementsDatabase(tileGrid: TileGrid) : BaseView(tileGrid, UiOptions.THEME) {
    init {
        val vbox = Components.vbox()
            .withSize(screen.size)
            .build()
        Elements.allCommandingElements()
            .forEach { (name, element) ->
                vbox.addComponent(
                    Components
                        .label()
                        .withText("$name: ${element.new().description}")
                )
            }
        screen.addComponent(vbox)
    }
}