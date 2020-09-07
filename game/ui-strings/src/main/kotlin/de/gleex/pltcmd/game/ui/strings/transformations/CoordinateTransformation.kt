package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.MainCoordinate

/**
 * Transformation used to display [Coordinate]s.
 */
internal val coordinateTransformation: Transformation<Coordinate> = { format: Format ->
    when (format) {
        Format.ICON -> Coordinate.SEPARATOR
        Format.SHORT3 -> formattedMainCoordinate
        Format.SHORT5 -> "(${formattedMainCoordinate})"
        Format.SIDEBAR, Format.FULL -> defaultTransformation(format)
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
            startsWith('-') -> "-"
            length > 1      -> "*"
            else            -> this
        }