package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [DefaultFrontendString] for any object that has no special [toFrontendString] method.
 */
fun Any.toFrontendString(length: FrontendString.Length = FrontendString.Length.FULL): FrontendString<Any> =
        DefaultFrontendString(this, length)

/**
 * Creates a [DefaultFrontendString] of this observable value. This method works for [Any] object when there is no
 * special override.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(length: FrontendString.Length = FrontendString.Length.FULL): FrontendString<T> =
        DefaultFrontendString(this, length)

@JvmName("toFrontendStringCoordinate")
fun ObservableValue<Coordinate>.toFrontendString(length: FrontendString.Length = FrontendString.Length.FULL): FrontendString<Coordinate> =
        CoordinateFrontendString(this, length)