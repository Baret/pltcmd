package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.Format

/**
 * Returns the shorter of both formats.
 */
fun minOf(first: Format, second: Format) =
        if(first.length <= second.length) {
            first
        } else {
            second
        }
