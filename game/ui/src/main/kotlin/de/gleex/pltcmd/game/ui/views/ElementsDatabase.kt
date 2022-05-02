package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.ColorRepository
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.fragments.ElementHierarchyFragment
import de.gleex.pltcmd.game.ui.fragments.ElementUnitsFragment
import de.gleex.pltcmd.game.ui.fragments.table.Table
import de.gleex.pltcmd.game.ui.fragments.table.column.Column
import de.gleex.pltcmd.game.ui.fragments.table.column.NumberColumn
import de.gleex.pltcmd.game.ui.fragments.table.column.TextColumn
import de.gleex.pltcmd.game.ui.strings.Format.*
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.util.debug.DebugFeature
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/**
 * This view contains a table to see and compare all elements currently present in the game.
 */
@DebugFeature("This is just a tool to have a good oversight of all elements in the game")
class ElementsDatabase(tileGrid: TileGrid) : BaseView(tileGrid, UiOptions.THEME) {
    init {
        val decor = ComponentDecorations.shadow()

        val elements = Elements.allCommandingElements().values.toList()
        val table = Table<CommandingElementBlueprint>(
            listOf(
                Column(" ", ICON) {
                    Components.icon().withIcon(TileRepository.Elements.marker(it.new(), Affiliation.Friendly)).build()
                },
                TextColumn("Name", SIDEBAR) { it.toFrontendString(SIDEBAR) },
                TextColumn(
                    "Corps",
                    SHORT5,
                    foregroundColorProvider = { ColorRepository.forCorps(it.corps) }) { it.corps },
                TextColumn("Kind", SHORT5, foregroundColorProvider = { ColorRepository.forKind(it.kind) }) { it.kind },
                TextColumn("Rung", SHORT5, foregroundColorProvider = { ColorRepository.forRung(it.rung) }) { it.rung },
                NumberColumn("Subs", SHORT5) { it.subordinates.size },
                NumberColumn("Units", SHORT5) { it.new().totalUnits },
                NumberColumn("Sold.", SHORT5) { it.new().totalSoldiers }
            ),
            elements,
            height = screen.height - decor.occupiedSize.height,
            rowSpacing = 1
        )

        val panelSize = (table.size + decor.occupiedSize).withHeight(screen.height)
        val tablePanel = Components
            .panel()
            .withDecorations(decor)
            .withPreferredSize(panelSize)
            .build()

        tablePanel.addFragment(table)

        val detailsPanelSize = screen.size.withWidth(screen.width - tablePanel.width)

        val detailsPanel = Components
            .vbox()
            .withPreferredSize(detailsPanelSize)
            .build()
            .apply {
                val halfSize = Size.create(contentSize.width, contentSize.height / 2)
                val selectedElement = table.selectedRow
                val hierarchyPanel = titledPanel("Hierarchy", halfSize)
                    .apply {
                        addFragment(
                            ElementHierarchyFragment(
                                selectedElement,
                                contentSize.width,
                                contentSize.height
                            )
                        )
                    }
                val unitsPanel = titledPanel("Units", halfSize)
                    .apply {
                        addFragment(
                            ElementUnitsFragment(
                                selectedElement,
                                contentSize.width,
                                contentSize.height
                            )
                        )
                    }
                addComponents(hierarchyPanel, unitsPanel)
            }

        screen.addComponent(Components
            .hbox()
            .withPreferredSize(screen.size)
            .build()
            .apply {
                addComponents(tablePanel, detailsPanel)
            })
    }

    private fun titledPanel(title: String, size: Size): Panel =
        Components
            .panel()
            .withDecorations(ComponentDecorations.box(BoxType.LEFT_RIGHT_DOUBLE, title))
            .withPreferredSize(size)
            .build()
}