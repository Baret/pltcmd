package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.amethyst.api.system.Facet
import kotlin.reflect.KClass


/**
 * Gets the [Attribute] of given type or throws an [IllegalStateException] if no attribute of this type is present.
 *
 * Be sure the attribute exists when using.
 *
 * @see hasAttribute
 */
internal fun <T : Attribute> AnyGameEntity.getAttribute(attribute: KClass<T>) =
        findAttribute(attribute).orElseThrow { IllegalStateException("Entity $this does not have an attribute of type $attribute") }

/**
 * Checks if this entity has the given attribute (shorthand version for findAttribute).
 */
internal fun <T : Attribute> AnyGameEntity.hasAttribute(attribute: KClass<T>): Boolean =
        findAttribute(attribute).isPresent

/**
 * Checks if this entity has the given behavior (shorthand version for findBehavior).
 */
internal fun <T: Behavior<GameContext>> AnyGameEntity.hasBehavior(behavior: KClass<T>): Boolean =
        findBehavior(behavior).isPresent

/**
 * Checks if this entity has the given facet (shorthand version for findFacet).
 */
internal fun <T: Facet<GameContext, *>> AnyGameEntity.hasFacet(facet: KClass<T>): Boolean =
        findFacet(facet).isPresent

/**
 * Adds the given attribute if no attribute of type [A] is present.
 */
internal inline fun <reified A : Attribute> AnyGameEntity.addIfMissing(attribute: A) {
    if(findAttribute(A::class).isEmpty()) {
        asMutableEntity().addAttribute(attribute)
    }
}

/**
 * Adds the given behavior if no attribute of type [B] is present.
 */
internal inline fun <reified B : Behavior<GameContext>> AnyGameEntity.addIfMissing(behavior: B) {
    if(findBehavior(B::class).isEmpty()) {
        asMutableEntity().addBehavior(behavior)
    }
}

/**
 * Adds the given facet if no attribute of type [F] is present.
 */
internal inline fun <reified F : Facet<GameContext, *>> AnyGameEntity.addIfMissing(facet: F) {
    if(findFacet(F::class).isEmpty()) {
        asMutableEntity().addFacet(facet)
    }
}