package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.AnyGameEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property

/** The location of an entity on the map. */
class PositionAttribute(initialPosition: Coordinate) : Attribute {
    val coordinate = createPropertyFrom(initialPosition)
}

var AnyGameEntity.coordinate: Property<Coordinate>
    get() = findAttribute(PositionAttribute::class).orElseThrow { IllegalStateException() }.coordinate
    set(value) {
        findAttribute(PositionAttribute::class).map {
            it.coordinate.updateFrom(value)
        }
    }