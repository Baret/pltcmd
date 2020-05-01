package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import org.hexworks.amethyst.api.entity.Entity

/**
 * A specific entity type using the [GameContext]
 */
typealias GameEntity<T> = Entity<T, GameContext>

/**
 * Unspecified entity type using the [GameContext]
 */
typealias AnyGameEntity = GameEntity<*>
