package de.gleex.pltcmd.game.engine.extensions

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.*
import mu.KotlinLogging
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.extensions.FacetWithContext
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.cobalt.databinding.api.extension.orElseThrow
import kotlin.reflect.KClass

private val log = KotlinLogging.logger {}

/**
 * Gets the [Attribute] of given type or throws an [IllegalStateException] if no attribute of this type is present.
 *
 * Be sure the attribute exists when using.
 *
 * @see hasAttribute
 */
internal fun <T : Attribute> AnyGameEntity.getAttribute(attribute: KClass<T>) =
    findAttributeOrNull(attribute).orElseThrow { IllegalStateException("Entity $this does not have an attribute of type $attribute") }

/**
 * Checks if this entity has the given attribute (shorthand version for findAttribute).
 */
internal fun <T : Attribute> AnyGameEntity.hasAttribute(attribute: KClass<T>): Boolean =
    findAttributeOrNull(attribute) != null

/**
 * Checks if this entity has the given behavior (shorthand version for findBehavior).
 */
internal fun <T : Behavior<GameContext>> AnyGameEntity.hasBehavior(behavior: KClass<T>): Boolean =
    findBehaviorOrNull(behavior) != null

/**
 * Checks if this entity has the given facet (shorthand version for findFacet).
 */
internal fun <T : FacetWithContext<GameContext>> AnyGameEntity.hasFacet(facet: KClass<T>): Boolean =
    findFacetOrNull(facet) != null

/**
 * Adds the given attribute if no attribute of type [A] is present.
 */
internal inline fun <reified A : Attribute> AnyGameEntity.addIfMissing(attribute: A) {
    if (findAttributeOrNull(A::class) == null) {
        asMutableEntity().addAttribute(attribute)
    }
}

/**
 * Adds the given behavior if no attribute of type [B] is present.
 */
internal inline fun <reified B : Behavior<GameContext>> AnyGameEntity.addIfMissing(behavior: B) {
    if (findBehaviorOrNull(B::class) == null) {
        asMutableEntity().addBehavior(behavior)
    }
}

/**
 * Adds the given facet if no attribute of type [F] is present.
 */
internal inline fun <reified F : FacetWithContext<GameContext>> AnyGameEntity.addIfMissing(facet: F) {
    if (findFacetOrNull(F::class) == null) {
        asMutableEntity().addFacet(facet)
    }
}

/**
 * Casts this entity to [E] when it's type is [T] and provides it to [whenType]. Else return `null`.
 *
 * **This functions should not be used directly!** Instead every entity type/typealias should
 * have its own `asSomeEntity()` function!
 *
 * This function does a cast based on the entity type. **So make sure that [E] is an entity with [EntityType] [T]!**
 *
 * @param E the type this entity is cast to
 * @param T when this entity has this [EntityType] it will be cast without checking
 * @param R the return type of [whenType]
 * @param whenType invoked with this entity as parameter when the type matches [T]
 */
internal inline fun <E : AnyGameEntity, reified T : EntityType, R> AnyGameEntity.tryCastTo(whenType: (E) -> R): R? =
    if (type is T) {
        @Suppress("UNCHECKED_CAST")
        whenType(this as E)
    } else {
        null
    }

/**
 * This function is handy for [Behavior]s that send messages to the entity. It works like [tryCastTo] except
 * that it gets a suspend function as parameter. [invocation] may call
 * [org.hexworks.amethyst.api.entity.Entity.receiveMessage] without leaving the current coroutine context.
 *
 * **This functions should not be used directly!** Instead every entity type/typealias should
 * have its own `asSomeEntity()` function!
 *
 * This function does a cast based on the entity type. **So make sure that [E] is an entity with [EntityType] [T]!**
 *
 * @return false when this entity is NOT of type [T]. The result of [invocation] otherwise.
 *
 * @see tryCastTo
 */
internal suspend inline fun <E : AnyGameEntity, reified T : EntityType, R> AnyGameEntity.castToSuspending(crossinline invocation: suspend (E) -> R): R? =
    if (type is T) {
        @Suppress("UNCHECKED_CAST")
        invocation(this as E)
    } else {
        log.warn { "$logIdentifier can not be cast to an entity of type ${T::class} because it has type ${type::class}" }
        null
    }

/** The unique name of this entity if it has one or something else to identify the object in the logs */
@Suppress("UNCHECKED_CAST")
internal val AnyGameEntity.logIdentifier: String
    // hopefully this generic top level code will not produce a dependency loop on the specific sub types ¯\_(ツ)_/¯
    get() = when (type) {
        is FOBType       -> "FOB ${(this as FOBEntity).radioCallSign}"
        is ElementType   -> "Element ${(this as ElementEntity).callsign.name}"
        is Communicating -> (this as CommunicatingEntity).radioCallSign.name
        else             -> "${type.name}_${System.identityHashCode(this)}"
    }
