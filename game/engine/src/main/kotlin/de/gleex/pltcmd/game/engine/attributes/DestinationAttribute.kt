package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.datatypes.Maybe

/** The destination of an entity on the map. */
class DestinationAttribute(initialDestination: Coordinate? = null) : Attribute {
    private val coordinateProperty = createPropertyFrom(Maybe.ofNullable(initialDestination))

    var coordinate: Maybe<Coordinate>
        get() = coordinateProperty.value
        set(value) {
            coordinateProperty.updateValue(value)
        }
}
