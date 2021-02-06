package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.ColorRepository
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.ComponentStyleSetBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.component.data.ComponentState
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile

/**
 * A table listing the given element blueprints.
 */
class ElementsTable(
    val size: Size = MIN_SIZE,
    allElements: List<CommandingElementBlueprint> = Elements.allCommandingElements().values.toList()
) : Fragment {

    companion object {
        private val log = LoggerFactory.getLogger(ElementsTable::class)

        /**
         * Maps the column header name that will be displayed to the [Format] to be used in that column.
         * The length of the format determines the width of the column.
         */
        private val columnConfig: Map<String, Format> = mapOf(
            " " to Format.ICON,
            "Name" to Format.SIDEBAR,
            "Corps" to Format.SHORT5,
            "Kind" to Format.SHORT5,
            "Rung" to Format.SHORT5,
            "Subs" to Format.SHORT5,
            "Units" to Format.SHORT5,
            "Sold." to Format.SHORT5
        )

        const val COLUMN_SPACING = 1
        const val ROW_SPACING = 0
        private const val ROW_HEIGHT = 1

        private val MIN_WIDTH = columnConfig.values.sumBy { it.length } + (columnConfig.size * COLUMN_SPACING)
        private const val MIN_HEIGHT = 6

        /**
         * The minimum [Size] this components needs.
         */
        val MIN_SIZE = Size.create(MIN_WIDTH, MIN_HEIGHT)
    }

    init {
        log.debug("Creating table with size $size, minWidth = $MIN_WIDTH")
        require(size.width >= MIN_WIDTH) {
            "ElementsTable needs a width of at least $MIN_WIDTH"
        }
        require(size.height >= MIN_HEIGHT) {
            "ElementsTable needs a height of at least $MIN_HEIGHT"
        }
    }

    private val headerPanel = Components
        .hbox()
        .withSize(size.width, 2)
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
        .withSpacing(ROW_SPACING)
        .withSize(size.width, size.height - headerPanel.size.height)
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
            headerPanel,
            tableVBox
        )
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

    private fun rowOf(elementBlueprint: CommandingElementBlueprint): HBox =
        Components
            .hbox()
            .withSpacing(COLUMN_SPACING)
            .withSize(tableVBox.contentSize.width, ROW_HEIGHT)
            .build()
            .apply {
                val elementInstance = elementBlueprint.new()
                addComponents(
                    iconCell(TileRepository.Elements.marker(elementInstance, Affiliation.Friendly)),
                    nameCell(Elements.nameOf(elementBlueprint)!!),
                    corpsCell(elementBlueprint.corps),
                    kindCell(elementBlueprint.kind),
                    rungCell(elementBlueprint.rung),
                    numberCell(elementBlueprint.subordinates.size),
                    numberCell(elementInstance.totalUnits),
                    numberCell(elementInstance.totalSoldiers)
                )
            }

    private fun nameCell(name: String): Label =
        cell(columnConfig["Name"]!!, name)

    private fun iconCell(icon: Tile): Icon =
        Components
            .icon()
            .withIcon(icon)
            .build()

    private fun corpsCell(corps: Corps): Component =
        cell(columnConfig["Corps"]!!, corps)
            .withColor(ColorRepository.forCorps(corps))

    private fun kindCell(kind: ElementKind): Label =
        cell(columnConfig["Kind"]!!, kind)
            .withColor(ColorRepository.forKind(kind))

    private fun rungCell(rung: Rung): Label =
        cell(columnConfig["Rung"]!!, rung)
            .withColor(ColorRepository.forRung(rung))

    private fun numberCell(number: Int): Label =
        with(Format.SHORT5) {
            cell(this, "$number".padStart(length))
        }

    private fun cell(format: Format, content: Any): Label {
        return Components
            .label()
            .withSize(format.length, ROW_HEIGHT)
            .build()
            .apply { withFrontendString(format, content) }
    }

    private fun clearTable() {
        rows.forEach { it.detach() }
    }

    private fun <C : Component> C.withColor(color: TileColor): C =
        apply {
            val newStyle = UiOptions.THEME.toContainerStyle().fetchStyleFor(ComponentState.DEFAULT)
                .withForegroundColor(color)
            val newComponentStyle = ComponentStyleSetBuilder.newBuilder()
                .withDefaultStyle(newStyle)
                .withActiveStyle(componentStyleSet.fetchStyleFor(ComponentState.ACTIVE))
                .withDisabledStyle(componentStyleSet.fetchStyleFor(ComponentState.DISABLED))
                .withFocusedStyle(componentStyleSet.fetchStyleFor(ComponentState.FOCUSED))
                .withMouseOverStyle(componentStyleSet.fetchStyleFor(ComponentState.HIGHLIGHTED))
                .build()
            componentStyleSet = newComponentStyle
        }
}
