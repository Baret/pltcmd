package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.special.CoordinateFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [DefaultFrontendString] for any object that has no special [toFrontendString] method.
 */
fun Any.toFrontendString(objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL): FrontendString<Any> =
        DefaultFrontendString(this, objectFormatter)

/**
 * Creates a [DefaultFrontendString] of this observable value. This method works for [Any] object when there is no
 * special override.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL): FrontendString<T> =
        DefaultFrontendString(this, objectFormatter)

@JvmName("toFrontendStringCoordinate")
fun ObservableValue<Coordinate>.toFrontendString(objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL): FrontendString<Coordinate> =
        CoordinateFrontendString(this, objectFormatter)