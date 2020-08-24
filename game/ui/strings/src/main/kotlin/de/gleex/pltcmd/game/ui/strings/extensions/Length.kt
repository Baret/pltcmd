package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString

/**
 * Returns the smaller of both lengths.
 */
fun minOf(first: FrontendString.Length, second: FrontendString.Length) =
        if(first.characters <= second.characters) {
            first
        } else {
            second
        }