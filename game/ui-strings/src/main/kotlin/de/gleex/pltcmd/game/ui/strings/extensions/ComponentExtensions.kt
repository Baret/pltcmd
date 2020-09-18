package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.Format
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
 * Converts all given [objects] to [FrontendString]s with the given [Format]. The strings are then "added together"
 * and the combination of all is used to update the text of this [TextHolder].
 */
fun TextHolder.withFrontendString(format: Format, vararg objects: Any) {
    val frontendStrings: Collection<FrontendString<*>> = objects.map {
        when (it) {
            is FrontendString<*>    -> it
            is ObservableValue<Any> -> it.toFrontendString(format)
            else                    -> it.toFrontendString(format)
        }
    }
    val combined: FrontendString<*> = frontendStrings
            .reduce { accumulatedStrings, nextString ->
                accumulatedStrings + nextString
            }
    withFrontendString(combined)
}

/**
 * Converts the given [ObservableValue] to a [FrontendString] and uses it for the text property
 * of this [TextHolder].
 *
 * @see withFrontendString
 */
fun TextHolder.withFrontendString(observable: ObservableValue<*>) =
        withFrontendString(observable.toFrontendString())
