package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.extensions.*
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.distance.kilometers
import de.gleex.pltcmd.util.measure.speed.Speed
import de.gleex.pltcmd.util.measure.speed.perHour
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
val MovableEntity.baseSpeed: Speed
    get() {
        val baseSpeed = getAttribute(MovementBaseSpeed::class).value
        return baseSpeed.kilometers.perHour
    }

/**
 * The current speed calculated by taking the [baseSpeed] and applying all [movementModifiers].
 */
val MovableEntity.currentSpeed: Speed
    get() = movementModifiers.fold(baseSpeed) { speed: Speed, modifier: MovementModifier -> modifier(speed) }

/** true if [currentSpeed] is not [Speed.ZERO] and we have a [destination] */
val MovableEntity.isMoving: Boolean
    get() = currentSpeed != Speed.ZERO && destination.isPresent

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

/**
 * Invokes the given suspend function when this entity is of type [Movable].
 *
 * @return the result of [whenMovable] when this entity is a [MovableEntity]. False otherwise.
 */
suspend fun AnyGameEntity.invokeWhenMovable(whenMovable: suspend (MovableEntity) -> Boolean): Boolean =
    castToSuspending<MovableEntity, Movable, Boolean>(whenMovable)
        .orElse(false)

/**
 * Invokes [whenElement] if this entity is an [MovableEntity]. When the type is not [Movable],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenElement]
 */
fun <R> AnyGameEntity.asMovableEntity(whenElement: (MovableEntity) -> R): Maybe<R> =
    tryCastTo<MovableEntity, Movable, R>(whenElement)
