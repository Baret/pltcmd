package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.special.CoordinateFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [DefaultFrontendString] for any object that has no special [toFrontendString] method.
 */
fun Any.toFrontendString(format: FrontendString.Format = FrontendString.Format.FULL): FrontendString<Any> =
        DefaultFrontendString(this, format)

/**
 * Creates a [DefaultFrontendString] of this observable value. This method works for [Any] object when there is no
 * special override.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(format: FrontendString.Format = FrontendString.Format.FULL): FrontendString<T> =
        DefaultFrontendString(this, format)

@JvmName("toFrontendStringCoordinate")
fun ObservableValue<Coordinate>.toFrontendString(format: FrontendString.Format = FrontendString.Format.FULL): FrontendString<Coordinate> =
        CoordinateFrontendString(this, format)
