package de.gleex.pltcmd.game.ui.fragments.table

import de.gleex.pltcmd.game.ui.fragments.table.column.Column
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventPhase

/**
 * A table lists values of type [M] in columns defined by [columns].
 *
 * @param columns the [Column] definitions of this table
 * @param values each value is displayed in a table row
 * @param M the type of the underlying model
 */
class Table<M : Any>(
    private val columns: List<Column<M, *>>,
    private val values: List<M>,
    private val height: Int = MIN_HEIGHT,
    private val columnSpacing: Int = 1,
    rowSpacing: Int = 1
) : Fragment {

    init {
        require(height >= MIN_HEIGHT) {
            "Table needs a height of at least $MIN_HEIGHT"
        }
    }

    private val selectedRowProperty: Property<M> = values.first().toProperty()

    val selectedRow: ObservableValue<M> = selectedRowProperty

    private val width = columns.sumBy { it.format.length } + (columns.size * columnSpacing)

    /**
     * The actual size of this table.
     */
    val size: Size = Size.create(width, height)

    private val headerPanel: HBox =
        newRow(
            columns
                .map { column ->
                    Components
                        .header()
                        .withSize(column.width, 1)
                        .withText(column.name)
                        .build()
                        .apply {
                            processMouseEvents(MouseEventType.MOUSE_CLICKED) { _, phase ->
                                if (phase == UIEventPhase.TARGET) {
                                    log.debug("Column header '${column.name}' has been clicked. A filter modal might have opened now :O")
                                }
                            }
                        }
                })

    private val rowPanel: VBox =
        Components
            .vbox()
            .withSize(width, height - headerPanel.height)
            .withSpacing(rowSpacing)
            .build()

    override val root: VBox =
        Components
            .vbox()
            .withSize(width, height)
            .withSpacing(0)
            .build()
            .apply {
                addComponents(headerPanel, rowPanel)
            }

    private val rows: MutableList<AttachedComponent> = mutableListOf()

    private val maxRows: Int = (rowPanel.height.toDouble() / (1 + rowSpacing)).toInt()

    init {
        log.debug("Creating table with ${Size.create(height, width)} for ${columns.size} columns and ${values.size} values. rowPanel.height = ${rowPanel.height}, maxRows = $maxRows")
        fillTable(values)
    }

    private fun fillTable(elements: List<M>) {
        clearTable()
        elements
            .asSequence()
            .map {
                newRowFor(it)
            }
            .take(maxRows)
            .forEach {
                rows.add(rowPanel.addComponent(it))
            }
    }

    private fun clearTable() {
        rows.forEach { it.detach() }
    }

    private fun newRowFor(value: M) =
        newRow(
            columns
                .map {
                    it.componentCreator(value)
                        .apply {
                            processMouseEvents(MouseEventType.MOUSE_CLICKED) { event, phase ->
                                if (phase == UIEventPhase.TARGET && event.button == 1) {
                                    selectedRowProperty.updateValue(value)
                                }
                            }
                        }
                }
        )

    private fun newRow(components: List<Component>): HBox =
        Components
            .hbox()
            .withSize(width, 1)
            .withSpacing(columnSpacing)
            .build()
            .apply {
                components.forEach {
                    addComponent(it)
                }
            }

    companion object {
        /**
         * The minimum height this fragment needs.
         */
        const val MIN_HEIGHT = 6

        private val log = LoggerFactory.getLogger(Table::class)
    }
}