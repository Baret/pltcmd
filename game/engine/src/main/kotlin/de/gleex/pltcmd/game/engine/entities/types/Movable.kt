package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This file contains code for entities that have the [DestinationAttribute].
 */

/** Type marker for entities that are [Positionable] and have the [DestinationAttribute] */
interface Movable : Positionable

/** Access to the value of the [DestinationAttribute] of a [GameEntity] */
var GameEntity<Movable>.destination: Maybe<Coordinate>
    get() = getAttribute(DestinationAttribute::class).coordinate
    internal set(value) {
        findAttribute(DestinationAttribute::class).map {
            it.coordinate = value
        }
    }

/** Check if a destination is set. */
val GameEntity<Movable>.hasNoDestination: Boolean
    get() = destination.isEmpty()

/** Clears the destination of this movable entity. */
internal fun GameEntity<Movable>.reachedDestination() {
    destination = Maybe.empty()
}
