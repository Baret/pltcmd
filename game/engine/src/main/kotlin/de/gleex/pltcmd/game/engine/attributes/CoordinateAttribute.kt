package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

class CoordinateAttribute(initialCoordinate: Coordinate) : Attribute {
    private val coordinateProperty = createPropertyFrom(initialCoordinate)

    // getter and setter
    var coordinate: Coordinate by coordinateProperty.asDelegate()
}