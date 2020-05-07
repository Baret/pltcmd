package de.gleex.pltcmd.game.engine.extensions

import org.hexworks.amethyst.api.Attribute
import kotlin.reflect.KClass


/**
 * Gets the [Attribute] of given type or throws an [IllegalStateException] if no attribute of this type is present.
 *
 * Be sure the attribute exists when using.
 */
internal fun <T : Attribute> AnyGameEntity.getAttribute(attribute: KClass<T>) =
        findAttribute(attribute).orElseThrow { IllegalStateException("Entity $this does not have an attribute of type $attribute") }
