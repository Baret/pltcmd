package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.callsign
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.extensions.FacetWithContext
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.reflect.KClass

private val log = LoggerFactory.getLogger("AnyGameEntity")

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
internal fun <T : Behavior<GameContext>> AnyGameEntity.hasBehavior(behavior: KClass<T>): Boolean =
    findBehavior(behavior).isPresent

/**
 * Checks if this entity has the given facet (shorthand version for findFacet).
 */
internal fun <T : FacetWithContext<GameContext>> AnyGameEntity.hasFacet(facet: KClass<T>): Boolean =
    findFacet(facet).isPresent

/**
 * Adds the given attribute if no attribute of type [A] is present.
 */
internal inline fun <reified A : Attribute> AnyGameEntity.addIfMissing(attribute: A) {
    if (findAttribute(A::class).isEmpty()) {
        asMutableEntity().addAttribute(attribute)
    }
}

/**
 * Adds the given behavior if no attribute of type [B] is present.
 */
internal inline fun <reified B : Behavior<GameContext>> AnyGameEntity.addIfMissing(behavior: B) {
    if (findBehavior(B::class).isEmpty()) {
        asMutableEntity().addBehavior(behavior)
    }
}

/**
 * Adds the given facet if no attribute of type [F] is present.
 */
internal inline fun <reified F : FacetWithContext<GameContext>> AnyGameEntity.addIfMissing(facet: F) {
    if (findFacet(F::class).isEmpty()) {
        asMutableEntity().addFacet(facet)
    }
}

/**
 * Casts this entity to [E] when it's type is [T] an provides it to [whenTrue]. Else it is provided
 * to [whenFalse] as is.
 *
 * **This functions should not be used directly!** Instead every entity type/typealias should
 * have its own `asSomeEntity()` function!
 *
 * **This function does an unchecked cast!**
 *
 * @param E the type this entity is cast to
 * @param T when this entity has this [EntityType] it will be cast without checking
 * @param R the return type of [whenTrue] and [whenFalse]
 * @param whenTrue invoked with this entity as parameter when the type matches [T]
 * @param whenFalse invoked with this entity as parameter when the type does not match [T]
 */
internal inline fun <E : AnyGameEntity, reified T : EntityType, R> AnyGameEntity.castTo(
    whenTrue: (E) -> R,
    whenFalse: (AnyGameEntity) -> R
): R {
    return if (type is T) {
        @Suppress("UNCHECKED_CAST")
        whenTrue(this as E)
    } else {
        whenFalse(this)
    }
}

/**
 * Casts this entity to [E] when it's type is [T] and provides it to [invocation]. When the type does
 * not match a warning will be logged because when calling this function you should be rather sure that
 * this entity has the correct type.
 *
 * **This functions should not be used directly!** Instead every entity type/typealias should
 * have its own `asSomeEntity()` function!
 *
 * **This function does an unchecked cast!**
 */
internal inline fun <E : AnyGameEntity, reified T : EntityType> AnyGameEntity.castTo(invocation: (E) -> Unit) =
    if (type is T) {
        @Suppress("UNCHECKED_CAST")
        invocation(this as E)
    } else {
        log.warn("$this can not be cast to an entity of type ${T::class} because it has type ${type::class}")
    }

/**
 * This function is handy for [Behavior]s that send messages to the entity. It works like [castTo] except
 * that it gets a suspend function as parameter. [invocation] may call
 * [org.hexworks.amethyst.api.entity.Entity.receiveMessage] without leaving the current coroutine context.
 *
 * **This functions should not be used directly!** Instead every entity type/typealias should
 * have its own `asSomeEntity()` function!
 *
 * **This function does an unchecked cast!**
 *
 * @return false when this entity is NOT of type [T]. The result of [invocation] otherwise.
 *
 * @see castTo
 */
internal suspend inline fun <E : AnyGameEntity, reified T : EntityType>
        AnyGameEntity.castToSuspending(
            crossinline invocation: suspend (E) -> Boolean
        ): Boolean =
    if (type is T) {
        @Suppress("UNCHECKED_CAST")
        invocation(this as E)
    } else {
        log.warn("$this can not be cast to an entity of type ${T::class} because it has type ${type::class}")
        false
    }


/** The unique name of the entity if it has one or something else to identify the object in the logs */
internal val AnyGameEntity.logIdentifier: String
// hopefully this generic top level code will not produce a dependency loop on the specific sub types ¯\_(ツ)_/¯
    get() = when (type) {
        ElementType -> (this as ElementEntity).callsign.name
        else        -> type.name + System.identityHashCode(this)
    }
