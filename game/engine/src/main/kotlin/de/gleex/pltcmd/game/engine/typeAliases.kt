package de.gleex.pltcmd.game.engine

import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.reflect.KClass

/**
 * Shortcut for every entity in the game.
 */
typealias AnyGameEntity = Entity<EntityType, GameContext>

/**
 * Gets the [Attribute] of given type or throws an [IllegalStateException] if no attribute of this type is present.
 */
fun <T: Attribute> AnyGameEntity.getAttribute(attribute: KClass<T>) = findAttribute(attribute).orElseThrow { IllegalStateException("Entity $this does not have an attribute of type $attribute") }

/**
 * An entity of [EntityType] T.
 */
typealias GameEntity<T> = Entity<T, GameContext>