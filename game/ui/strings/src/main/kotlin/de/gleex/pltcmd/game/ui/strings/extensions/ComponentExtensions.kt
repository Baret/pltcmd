package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.behavior.TextHolder

fun TextHolder.withFrontendString(frontendString: FrontendString<*>) {
    textProperty.updateFrom(frontendString, true)
}

fun TextHolder.withFrontendString(observable: ObservableValue<*>) {
    textProperty.updateFrom(observable.toFrontendString(), true)
}