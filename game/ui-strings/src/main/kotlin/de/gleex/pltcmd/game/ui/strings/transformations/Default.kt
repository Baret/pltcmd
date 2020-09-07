package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString

/**
 * Default transformation for [FrontendString]s that simply uses toString(), trims the original and appends "..."
 * if it is too long. For very short lengths the string is simply cut off.
 */
internal val defaultTransformation: Any.(Format) -> String = { format: Format ->
    val string = toString()
    if (string.length <= format.length) {
        string
    } else {
        if (format.length >= 6) {
            string.substring(0, format.length - 3)
                    .padEnd(format.length, '.')
        } else {
            string.substring(0, format.length)
        }
    }
}