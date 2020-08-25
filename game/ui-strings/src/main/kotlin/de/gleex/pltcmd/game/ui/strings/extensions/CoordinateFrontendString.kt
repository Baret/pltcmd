package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.SimpleFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue

class CoordinateFrontendString(
        originalObject: ObservableValue<Coordinate>,
        length: FrontendString.Length = FrontendString.Length.FULL
) : SimpleFrontendString<Coordinate>(originalObject, length) {

    private val short3: Coordinate.() -> String = {"${eastingFromLeft.toString().first()}|${northingFromBottom.toString().first()}"}
    private val short5: Coordinate.() -> String = {
        if(toMainCoordinate().toString().length <= 5) {
            "${toMainCoordinate()}"
        } else {
            short3.invoke(this)
        }
    }

    override val mapping: Map<FrontendString.Length, Coordinate.() -> String> = mapOf(
                    FrontendString.Length.SHORT5 to short5,
                    FrontendString.Length.SHORT3 to short3,
                    FrontendString.Length.ICON to {_:Coordinate -> "|"}
            )
}