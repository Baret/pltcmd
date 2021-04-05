package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.GameConstants
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.composites.GameLogo
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Container
import org.hexworks.zircon.api.extensions.isEnabled
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.ComponentEvent
import org.hexworks.zircon.api.view.base.BaseView

/**
 * Displays a centered menu with the given entries.
 */
class MenuView(tileGrid: TileGrid, entries: List<MenuEntry>) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    init {
        val header = createHeader()
        val menu = createMenu(entries)
        val footer = createFooter()
        screen.addComponents(header, menu, footer)
    }

    open fun createHeader(): Component {
        return Components.header()
            .withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER)
            .withText(GameLogo.asText)
            .build()
    }

    open fun createMenu(entries: List<MenuEntry>): Component {
        val maxLabelWidth =
            (entries.maxOfOrNull { it.label.length } ?: 0) + 2// + 2 because the button uses brackets left and right

        val menuBox = Components.vbox()
            .withSize(maxLabelWidth, entries.size)
            .withAlignmentWithin(screen, ComponentAlignment.CENTER)
            .build()
        entries.forEach(menuBox::addEntry)
        return menuBox
    }

    open fun createFooter(): Component {
        return Components.label()
            .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_LEFT)
            .withText(GameConstants.AppInfo.version)
            .build()
    }

}

data class MenuEntry(val label: String, val enabled: Boolean, val eventHandler: (ComponentEvent) -> Unit)

/**
 * Adds a button to this container. The button will have the text of the given entry which also handles events and determines if it is enabled.
 */
fun Container.addEntry(entry: MenuEntry) {
    val button = Components
        .button()
        .withText(entry.label)
        .build()
    addComponent(button)
    // disable
    button.apply {
        isEnabled = entry.enabled
        if (entry.enabled) {
            onActivated(entry.eventHandler)
        }
    }

}
