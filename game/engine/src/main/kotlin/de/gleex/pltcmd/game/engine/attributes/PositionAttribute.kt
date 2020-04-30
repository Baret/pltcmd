package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.Positionble
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property

/** The location of an entity on the map. */
class PositionAttribute(initialPosition: Coordinate) : Attribute {
    val coordinate = createPropertyFrom(initialPosition)
}

var GameEntity<Positionble>.coordinate: Property<Coordinate>
    get() = getAttribute(PositionAttribute::class).coordinate
    set(value) {
        findAttribute(PositionAttribute::class).map {
            it.coordinate.updateFrom(value)
        }
    }