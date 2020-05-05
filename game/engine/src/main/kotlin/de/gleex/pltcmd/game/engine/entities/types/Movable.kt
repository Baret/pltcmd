package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.extensions.MovableEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This file contains code for entities that have the [DestinationAttribute].
 */

/** Type marker for entities that are [Positionable] and have the [DestinationAttribute] */
interface Movable : Positionable

/** Access to the value of the [DestinationAttribute] of a [MovableEntity] */
var MovableEntity.destination: Maybe<Coordinate>
    get() = getAttribute(DestinationAttribute::class).coordinate
    internal set(value) {
        getAttribute(DestinationAttribute::class).coordinate = value
    }

/** Check if a destination is set. */
val MovableEntity.hasNoDestination: Boolean
    get() = destination.isEmpty()

/** Clears the destination of this movable entity. */
internal fun MovableEntity.reachedDestination() {
    destination = Maybe.empty()
}
