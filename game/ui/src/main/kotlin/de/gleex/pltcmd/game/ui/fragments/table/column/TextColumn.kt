package de.gleex.pltcmd.game.ui.fragments.table.column

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Label

/**
 * This [Column] creates a [Label] with a frontend string obtained from [componentCreator].
 */
open class TextColumn<T : Any, V: Any>(
    name: String,
    format: Format,
    valueAccessor: (T) -> V
): Column<T, Label>(name, format, {
    Components
        .label()
        .withSize(format.length, 1)
        .build()
        .apply { withFrontendString(format, valueAccessor(it)) }
})