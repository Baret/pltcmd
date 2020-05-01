package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.Maybe

/** The destination of an entity on the map. */
internal class DestinationAttribute(initialDestination: Coordinate? = null) : Attribute {
    var coordinate = Maybe.ofNullable(initialDestination)
}
