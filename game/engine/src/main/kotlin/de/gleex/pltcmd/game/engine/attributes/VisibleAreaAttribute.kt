package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.hexworks.amethyst.api.Attribute

/** All tiles that are currently visible for an entity and the point from where they were detected. */
internal class VisibleAreaAttribute(var from: Coordinate, var area: CoordinateArea) : Attribute {

    override fun toString(): String {
        return "VisibleAreaAttribute(from=$from, area=$area)"
    }

}
