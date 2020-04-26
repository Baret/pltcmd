package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The destination of an entity on the map. */
class DestinationAttribute(initialDestination: Coordinate) : Attribute {
    val coordinate = createPropertyFrom(initialDestination)
}
