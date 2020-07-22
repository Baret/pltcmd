package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.attributes.flags.Halted
import org.hexworks.amethyst.api.Attribute
import kotlin.reflect.KClass


/**
 * Gets the [Attribute] of given type or throws an [IllegalStateException] if no attribute of this type is present.
 *
 * Be sure the attribute exists when using.
 */
internal fun <T : Attribute> AnyGameEntity.getAttribute(attribute: KClass<T>) =
        findAttribute(attribute).orElseThrow { IllegalStateException("Entity $this does not have an attribute of type $attribute") }

/**
 * Checks if this entity has the given attribute (shorthand version for findAttribute).
 */
internal fun <T : Attribute> AnyGameEntity.hasAttribute(attribute: KClass<T>): Boolean =
        findAttribute(Halted::class).isPresent