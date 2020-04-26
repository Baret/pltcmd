package de.gleex.pltcmd.game.engine

import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

/**
 * Shortcut for every entity in the game.
 */
typealias AnyGameEntity = Entity<EntityType, GameContext>

/**
 * An entity of [EntityType] T.
 */
typealias GameEntity<T> = Entity<T, GameContext>