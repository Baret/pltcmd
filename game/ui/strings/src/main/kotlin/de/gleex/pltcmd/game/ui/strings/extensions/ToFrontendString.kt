package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import org.hexworks.cobalt.databinding.api.value.ObservableValue

fun Any.toFrontendString(length: FrontendString.Length = FrontendString.Length.FULL): FrontendString<Any> =
        DefaultFrontendString(this, length)

fun ObservableValue<Any>.toFrontendString(length: FrontendString.Length = FrontendString.Length.FULL) =
        DefaultFrontendString(this, length)