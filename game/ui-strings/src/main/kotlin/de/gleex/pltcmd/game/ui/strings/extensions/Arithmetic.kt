package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Converts the given observable to a [FrontendString] and adds it to this.
 *
 * @see [FrontendString.plus]
 */
operator fun FrontendString<Any>.plus(observable: ObservableValue<Any>) =
        this + observable.toFrontendString()

/**
 * Returns the shorter of both formats.
 */
fun minOf(first: FrontendString.Format, second: FrontendString.Format) =
        if(first.length <= second.length) {
            first
        } else {
            second
        }
