package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.NewFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.MainCoordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [DefaultFrontendString] for any object that has no special [toFrontendString] method.
 */
fun Any.toFrontendString(format: Format = Format.FULL): FrontendString<Any> =
        DefaultFrontendString(this, format)

/**
 * Creates a [DefaultFrontendString] of this observable value. This method works for [Any] object when there is no
 * special override.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(format: Format = Format.FULL): FrontendString<T> =
        DefaultFrontendString(this, format)

@JvmName("toFrontendStringCoordinate")
fun ObservableValue<Coordinate>.toFrontendString(format: Format = Format.FULL) =
        NewFrontendString<Coordinate>(this, format) { objectToTransform: Coordinate, transformationFormat: Format ->
            when(transformationFormat) {
                Format.ICON                 -> Coordinate.SEPARATOR
                Format.SHORT3               -> objectToTransform.formattedMainCoordinate
                Format.SHORT5               -> "(${objectToTransform.formattedMainCoordinate})"
                Format.SIDEBAR, Format.FULL -> objectToTransform.toString()
            }
        }

/**
 * The coordinate translated to a main coordinated and formatted as follows for easting and northing.
 * The result is a string of length 3.
 *
 * - When it has one digit, the digit
 * - When it is negative a minus (-)
 * - When it has more than one digit a star (*)
 */
private val Coordinate.formattedMainCoordinate: String
    get() {
        val mainCoordinate = toMainCoordinate()
        val easting = mainCoordinate.eastingFromLeft.toString()
        val northing = mainCoordinate.northingFromBottom.toString()
        return "${easting.toSingleDigit()}${MainCoordinate.SEPARATOR}${northing.toSingleDigit()}"
    }

private fun String.toSingleDigit() =
        when {
            startsWith('-')     -> "-"
            length > 1          -> "*"
            else                -> this
        }