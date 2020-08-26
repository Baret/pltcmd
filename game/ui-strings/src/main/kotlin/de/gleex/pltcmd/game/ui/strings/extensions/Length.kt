package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString

/**
 * Returns the smaller of both lengths.
 */
fun minOf(first: FrontendString.ObjectFormatter, second: FrontendString.ObjectFormatter) =
        if(first.length <= second.length) {
            first
        } else {
            second
        }