package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.ElementType
import org.hexworks.amethyst.api.entity.Entity

/**
 * Entity with a specific type using the [GameContext]
 */
internal typealias GameEntity<T> = Entity<T, GameContext>

/**
 * Entity with unspecified type using the [GameContext]
 */
typealias AnyGameEntity = GameEntity<*>

typealias ElementEntity = GameEntity<ElementType>