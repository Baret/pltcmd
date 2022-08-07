package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.behavior.TextOverride

/**
 * Updates the text of this [TextOverride] when the given [FrontendString] changes.
 */
fun TextOverride.withFrontendString(frontendString: FrontendString<*>) {
    textProperty.updateFrom(frontendString, true)
}

/**
 * Converts all given [objects] to [FrontendString]s with the given [Format]. The strings are then "added together"
 * and the combination of all is used to update the text of this [TextOverride].
 */
fun TextOverride.withFrontendString(format: Format, vararg objects: Any) {
    val frontendStrings: Collection<FrontendString<*>> = objects.map {
        when (it) {
            is FrontendString<*>    -> it
            is ObservableValue<*> -> it.toFrontendString(format)
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
 * of this [TextOverride].
 *
 * @see withFrontendString
 */
fun TextOverride.withFrontendString(observable: ObservableValue<*>) =
        withFrontendString(observable.toFrontendString())
