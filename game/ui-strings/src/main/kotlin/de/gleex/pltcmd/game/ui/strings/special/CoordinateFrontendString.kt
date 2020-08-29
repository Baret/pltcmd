package de.gleex.pltcmd.game.ui.strings.special

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A [FrontendString] for a [Coordinate].
 */
class CoordinateFrontendString(
        originalObject: ObservableValue<Coordinate>,
        format: Format = Format.FULL
) : DefaultFrontendString<Coordinate>(originalObject, format) {
    constructor(
            originalObject: Coordinate,
            format: Format = Format.FULL
    ) : this(originalObject.toProperty(), format)

    override fun Format.invoke(objectToTransform: Coordinate): String =
            when(this) {
                Format.ICON                 -> "|"
                Format.SHORT3               -> objectToTransform.formattedMainCoordinate
                Format.SHORT5               -> "(${objectToTransform.formattedMainCoordinate})"
                Format.SIDEBAR, Format.FULL -> objectToTransform.toString()
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
            return "${easting.toSingleDigit()}|${northing.toSingleDigit()}"
        }

    private fun String.toSingleDigit() =
        when {
            startsWith('-') -> "-"
            length > 1           -> "*"
            else                 -> this
        }
}