package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.MovementPath
import de.gleex.pltcmd.game.engine.attributes.MovementProgress
import de.gleex.pltcmd.game.engine.attributes.stats.ElementMovementSpeed
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

var MovableEntity.movementProgress: Double
    get() = getAttribute(MovementProgress::class).progressInPercent
    internal set(value) {
        getAttribute(MovementProgress::class).progressInPercent = value
    }

val MovableEntity.baseSpeedInKph: Double
    get() = getAttribute(ElementMovementSpeed::class).minimum

/** Check if a destination is set. */
val MovableEntity.hasNoDestination: Boolean
    get() = destination.isEmpty()
