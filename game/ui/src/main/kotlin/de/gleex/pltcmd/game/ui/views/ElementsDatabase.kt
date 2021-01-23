package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.components.ElementsTable
import de.gleex.pltcmd.game.ui.entities.ColorRepository
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.fragments.table.Table
import de.gleex.pltcmd.game.ui.fragments.table.column.Column
import de.gleex.pltcmd.game.ui.fragments.table.column.NumberColumn
import de.gleex.pltcmd.game.ui.fragments.table.column.TextColumn
import de.gleex.pltcmd.game.ui.strings.Format.*
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
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

        val table = Table<CommandingElementBlueprint>(
            listOf(
                Column(" ", ICON) {
                    Components.icon().withIcon(TileRepository.Elements.marker(it.new(), Affiliation.Friendly)).build()
                },
                TextColumn("Name", SIDEBAR) { it.toFrontendString(SIDEBAR) },
                TextColumn("Corps", SHORT5, foregroundColorProvider = { ColorRepository.forCorps(it.corps) }) { it.corps },
                TextColumn("Kind", SHORT5, foregroundColorProvider = { ColorRepository.forKind(it.kind) }) { it.kind },
                TextColumn("Rung", SHORT5, foregroundColorProvider = { ColorRepository.forRung(it.rung) }) { it.rung },
                NumberColumn("Subs", SHORT5) { it.subordinates.size },
                NumberColumn("Units", SHORT5) { it.new().totalUnits },
                NumberColumn("Sold.", SHORT5) { it.new().totalSoldiers }
            ),
            Elements.allCommandingElements().values.toList(),
            height = tablePanel.contentSize.height
        )

//        tablePanel.addFragment(ElementsTable(tablePanel.contentSize))
        tablePanel.addFragment(table)

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