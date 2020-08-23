package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.datatypes.Maybe
import java.util.*

/**
 * This file contains code for entities that have the [DestinationAttribute].
 */

/** Type marker for entities that are [Positionable] and can update their position by moving. */
interface Movable : Positionable
typealias MovableEntity = GameEntity<Movable>

/**
 * The destination of an entity determined by its current [movementPath]
 */
val MovableEntity.destination: Maybe<Coordinate>
    get() {
        return Maybe.ofNullable(movementPath.firstOrNull())
    }

/**
 * The currently planned path (or rather the rest of it).
 */
var MovableEntity.movementPath: Stack<Coordinate>
    get() = getAttribute(MovementPath::class).path
    internal set(value) {
        getAttribute(MovementPath::class).path = value
    }

internal val MovableEntity.movementProgress: MovementProgress
    get() = getAttribute(MovementProgress::class)

/**
 * The base speed determined by the [MovementBaseSpeed] attribute.
 */
val MovableEntity.baseSpeedInKph: Double
    get() = getAttribute(MovementBaseSpeed::class).value

/**
 * The current speed calculated by taking the [baseSpeedInKph] and applying all [movementModifiers].
 */
val MovableEntity.currentSpeedInKph: Double
    get() = movementModifiers.fold(baseSpeedInKph) { speed: Double, modifier: MovementModifier -> modifier(speed) }

/** Check if a destination is set. */
val MovableEntity.hasNoDestination: Boolean
    get() = destination.isEmpty()

/**
 * All currently active [MovementModifier]s this entity has.
 */
val MovableEntity.movementModifiers: Sequence<MovementModifier>
    get() = attributes
            .filter { it is MovementModifier }
            .map { it as MovementModifier }

/**
 * True if this entity is currently able to move because it has no [movementModifiers] of type [de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier.Prevention].
 *
 * @see canNotMove
 */
val MovableEntity.canMove: Boolean
    get() = movementModifiers.none { it is MovementModifier.Prevention }

/**
 * True if this entity is currently unable to move because it has [movementModifiers] of type [de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier.Prevention].
 *
 * @see canMove
 */
val MovableEntity.canNotMove: Boolean
    get() = canMove.not()