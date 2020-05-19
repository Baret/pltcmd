package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import org.hexworks.amethyst.api.entity.Entity

/**
 * Entity with a specific type using the [GameContext]
 */
internal typealias GameEntity<T> = Entity<T, GameContext>

/**
 * Entity with unspecified type using the [GameContext]
 */
typealias AnyGameEntity = GameEntity<*>

// typed entities
typealias PositionableEntity = GameEntity<Positionable>
typealias MovableEntity = GameEntity<Movable>
typealias ElementEntity = GameEntity<ElementType>
