package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

class CoordinateAttribute(initialCoordinate: Coordinate) : Attribute {
    val coordinate = createPropertyFrom(initialCoordinate)
}