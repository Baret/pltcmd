package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The location of an entity on the map. */
class PositionAttribute(initialPosition: Coordinate) : Attribute {
    val coordinate = createPropertyFrom(initialPosition)
}
