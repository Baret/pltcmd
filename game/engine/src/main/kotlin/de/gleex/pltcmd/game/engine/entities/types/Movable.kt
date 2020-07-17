package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.MovementPath
import de.gleex.pltcmd.game.engine.attributes.MovementProgress
import de.gleex.pltcmd.game.engine.attributes.MovementSpeed
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.datatypes.Maybe
import java.util.*

/**
 * This file contains code for entities that have the [DestinationAttribute].
 */

/** Type marker for entities that are [Positionable] and have the [DestinationAttribute] */
interface Movable : Positionable
typealias MovableEntity = GameEntity<Movable>

/** Access to the value of the [DestinationAttribute] of a [MovableEntity] */
var MovableEntity.destination: Maybe<Coordinate>
    get() = getAttribute(DestinationAttribute::class).coordinate
    internal set(value) {
        getAttribute(DestinationAttribute::class).coordinate = value
    }

/**
 * The currently planned path (or rather the rest of it).
 */
var MovableEntity.movementPath: Stack<Coordinate>
    get() = getAttribute(MovementPath::class).path
    internal set(value) {
        getAttribute(MovementPath::class).path = value
    }

var MovableEntity.movementProgress: Double
    get() = getAttribute(MovementProgress::class).progressInPercent
    internal set(value) {
        getAttribute(MovementProgress::class).progressInPercent = value
    }

val MovableEntity.baseSpeedInKph: Double
    get() = getAttribute(MovementSpeed::class).baseSpeedInKph

/** Check if a destination is set. */
val MovableEntity.hasNoDestination: Boolean
    get() = destination.isEmpty()

/** Clears the destination of this movable entity. */
internal fun MovableEntity.reachedDestination() {
    destination = Maybe.empty()
}
