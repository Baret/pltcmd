package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.behavior.TextHolder

/**
 * Updates the text of this [TextHolder] when the given [FrontendString] changes.
 */
fun TextHolder.withFrontendString(frontendString: FrontendString<*>) {
    textProperty.updateFrom(frontendString, true)
}

/**
 * Converts the given [ObservableValue] to a [FrontendString] and uses it for the text property
 * of this [TextHolder].
 *
 * @see withFrontendString
 */
fun TextHolder.withFrontendString(observable: ObservableValue<*>) =
    withFrontendString(observable.toFrontendString())
