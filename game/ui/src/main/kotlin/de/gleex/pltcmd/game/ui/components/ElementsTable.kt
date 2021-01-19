package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
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
    }

    init {
        log.debug("Creating table with size $size")
    }

    private val filterPanel: Panel = Components
        .panel()
        .withSize(size.width, 1)
        .build()
        .also {
            it.addComponent(Components.label().withText("Filters come here"))
        }

    private val tableVBox: VBox = Components
        .vbox()
        .withSpacing(0)
        .withSize(size.width, size.height - filterPanel.size.height)
        .withDecorations(ComponentDecorations.box())
        .build()

    private val maxRows = tableVBox.contentSize.height

    private val rows: MutableList<AttachedComponent> = mutableListOf()

    override val root: VBox = Components
        .vbox()
        .withSpacing(0)
        .withSize(size)
        .build()

    init {
        root.addComponents(filterPanel, tableVBox)
        fillTable(allElements)
    }

    private fun fillTable(elements: List<CommandingElementBlueprint>) {
        clearTable()
        elements
            .forEach {
                rows.add(tableVBox.addComponent(rowOf(it)))
            }
    }

    private fun rowOf(elementBlueprint: CommandingElementBlueprint): Panel {
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