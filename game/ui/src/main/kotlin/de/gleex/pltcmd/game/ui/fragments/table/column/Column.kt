package de.gleex.pltcmd.game.ui.fragments.table.column

import de.gleex.pltcmd.game.ui.strings.Format
import org.hexworks.zircon.api.component.Component

open class Column<T: Any, V: Component>(
    val name: String,
    val format: Format,
    val componentCreator: (T) -> V
) {
    val width = format.length
}