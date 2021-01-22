package de.gleex.pltcmd.game.ui.fragments.table.column

import de.gleex.pltcmd.game.ui.strings.Format

/**
 * A [Column] that displays an Int right padded.
 */
class NumberColumn<T : Any>(
    name: String,
    format: Format,
    valueAccessor: (T) -> Int
) :
    TextColumn<T, String>(
        name,
        format,
        { null },
        { null },
        { "${valueAccessor(it)}".padStart(format.length) }
    )