package de.gleex.pltcmd.game.ui.fragments.table.column

import de.gleex.pltcmd.game.ui.strings.Format
import org.hexworks.zircon.api.component.Component

/**
 * The column definition of a [de.gleex.pltcmd.game.ui.fragments.table.Table].
 *
 * @param name the name of the column, used as column header
 * @param format the [Format] defines the width of the column and also the base for the transformation of a cell value
 * @param componentCreator turns a row value into a [Component] to be displayed in the table
 */
open class Column<T: Any, V: Component>(
    val name: String,
    val format: Format,
    val componentCreator: (T) -> V
) {
    val width = format.length
}