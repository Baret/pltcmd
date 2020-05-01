package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * This file contains code for entities that have the [PositionAttribute].
 */

/** Type marker for entities that have the PositionAttribute */
interface Positionable : EntityType

/** Access to the value of the [PositionAttribute] of a [GameEntity] */
var GameEntity<Positionable>.coordinate: Property<Coordinate>
    get() = getAttribute(PositionAttribute::class).coordinate
    set(value) {
        findAttribute(PositionAttribute::class).map {
            it.coordinate.updateFrom(value)
        }
    }

/** Sets the position of this Positionable entity. */
fun GameEntity<Positionable>.placeAt(newPosition: Coordinate) {
    coordinate.updateValue(newPosition)
}
