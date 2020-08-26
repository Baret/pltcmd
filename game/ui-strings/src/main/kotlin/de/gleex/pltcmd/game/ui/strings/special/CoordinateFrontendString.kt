package de.gleex.pltcmd.game.ui.strings.special

import de.gleex.pltcmd.game.ui.strings.DefaultFrontendString
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A [FrontendString] for a [Coordinate].
 */
class CoordinateFrontendString(
        originalObject: ObservableValue<Coordinate>,
        objectFormatter: FrontendString.ObjectFormatter = FrontendString.ObjectFormatter.FULL
) : DefaultFrontendString<Coordinate>(originalObject, objectFormatter) {

    override fun FrontendString.ObjectFormatter.invoke(objectToTransform: Coordinate): String =
            when(this) {
                FrontendString.ObjectFormatter.ICON    -> "|"
                FrontendString.ObjectFormatter.SHORT3  -> "${objectToTransform.eastingFromLeft.toString().first()}|${objectToTransform.northingFromBottom.toString().first()}"
                FrontendString.ObjectFormatter.SHORT5  -> {
                    val mainCoordinateString = objectToTransform.toMainCoordinate().toString()
                    if(mainCoordinateString.length <= length) {
                        mainCoordinateString
                    } else {
                        FrontendString.ObjectFormatter.SHORT3(objectToTransform)
                    }
                }
                FrontendString.ObjectFormatter.SIDEBAR, FrontendString.ObjectFormatter.FULL -> objectToTransform.toString()
            }
}