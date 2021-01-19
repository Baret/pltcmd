package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.AttachedComponent
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Size

class ElementsTable(
    val size: Size,
    private val allElements: List<CommandingElementBlueprint> = Elements.allCommandingElements().values.toList()
) : Fragment {

    companion object {
        private val log = LoggerFactory.getLogger(ElementsTable::class)

        private val columnConfig: Map<String, Format> = mapOf(
            "Name" to Format.SIDEBAR,
            "Corps" to Format.SHORT5,
            "Type" to Format.SHORT5,
            "Rung" to Format.SHORT5,
            "Units" to Format.SHORT5,
            "Sold." to Format.SHORT5
        )

        private val COLUMN_SPACING = 1
    }

    init {
        val minWidth = columnConfig.values.sumBy { it.length } + (columnConfig.size * COLUMN_SPACING)
        log.debug("Creating table with size $size, minWidth = $minWidth")
        require(size.width >= minWidth) {
            "ElementsTable needs a width of at least $minWidth"
        }
    }

    private val filterPanel: Panel = Components
        .panel()
        .withSize(size.width, 1)
        .build()
        .also {
            it.addComponent(Components.label().withText("Filters come here"))
        }

    private val headerPanel = Components
        .hbox()
        .withSize(size.width, 1)
        .withSpacing(COLUMN_SPACING)
        .build()
        .apply {
            columnConfig
                .entries
                .map { (columnName, format) ->
                    Components
                        .header()
                        .withSize(format.length, 1)
                        .build()
                        .apply {
                            withFrontendString(format, columnName)
                        }
                }
                .forEach { addComponent(it) }
        }

    private val tableVBox: VBox = Components
        .vbox()
        .withSpacing(0)
        .withSize(size.width, size.height - filterPanel.size.height - headerPanel.size.height)
        .build()

    private val maxRows = tableVBox.contentSize.height

    private val rows: MutableList<AttachedComponent> = mutableListOf()

    override val root: VBox = Components
        .vbox()
        .withSpacing(0)
        .withSize(size)
        .build()

    init {
        root.addComponents(
            filterPanel,
            headerPanel,
            tableVBox)
        fillTable(allElements)
    }

    private fun fillTable(elements: List<CommandingElementBlueprint>) {
        clearTable()
        elements
            .forEach {
                addRow(it)
            }
    }

    private fun addRow(element: CommandingElementBlueprint) {
        if (rows.size < maxRows) {
            rows.add(tableVBox.addComponent(rowOf(element)))
        }
    }

    private fun rowOf(elementBlueprint: CommandingElementBlueprint): Panel {
        val row = Components
            .hbox()
            .withSpacing(1)
            .withSize(tableVBox.contentSize.width, 1)
            .build()

        return Components
            .panel()
            .withSize(tableVBox.contentSize.width, 1)
            .build()
            .also {
                it.addComponent(
                    Components
                        .label()
                        .withText("Element: ${Elements.nameOf(elementBlueprint)}")
                )
            }
    }

    private fun clearTable() {
        rows.forEach { it.detach() }
    }
}