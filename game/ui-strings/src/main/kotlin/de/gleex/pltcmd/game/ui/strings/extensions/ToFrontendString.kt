package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.transformations.coordinateTransformation
import de.gleex.pltcmd.game.ui.strings.transformations.defaultTransformation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [FrontendString] for any object that has no special [toFrontendString] method.
 */
fun <T: Any> T.toFrontendString(format: Format = Format.FULL): FrontendString<T> =
        toProperty().toFrontendString(format)

/**
 * Creates a [FrontendString] of this observable value. This method works for [Any] object when there is no
 * transformations override.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(format: Format = Format.FULL): FrontendString<T> =
        FrontendString(this, format, defaultTransformation)

/**
 * Creates a [FrontendString] of this coordinate.
 */
fun Coordinate.toFrontendString(format: Format = Format.FULL): FrontendString<Coordinate> =
        toProperty().toFrontendString(format)

/**
 * Creates a [FrontendString] of this observable coordinate.
 */
@JvmName("toFrontendStringCoordinate")
fun ObservableValue<Coordinate>.toFrontendString(format: Format = Format.FULL) =
        FrontendString(this, format, coordinateTransformation)

