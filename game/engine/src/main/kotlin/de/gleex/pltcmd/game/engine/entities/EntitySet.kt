package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import org.hexworks.amethyst.api.entity.EntityType

/**
 * A set of [GameEntity]s with a couple of handy methods to query for specific entities.
 *
 * @param T the [EntityType] of all entities in this set.
 */
class EntitySet<T : EntityType>(initialEntities: Iterable<GameEntity<T>> = emptySet()) : Set<GameEntity<T>> {

    private var allEntities: PersistentSet<GameEntity<T>> = initialEntities.toPersistentSet()

    override val size: Int
        get() = allEntities.size

    /**
     * Filters these entities for the given [EntityType] and casts them to [GameEntity]<S>.
     *
     * @param S the [EntityType] that the entities in the resulting [EntitySet] should have.
     * @param predicate optional predicate to further filter the cast entities.
     * @return a new [EntitySet] with entities of type [S]
     */
    inline fun <reified S : EntityType> filterTyped(noinline predicate: (GameEntity<S>) -> Boolean = { true }): EntitySet<S> {
        return asSequence()
                .filter { entity -> entity.type is S }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as GameEntity<S>
                }
                .filter(predicate)
                .toEntitySet()
    }

    /**
     * Filters these entities by type [ElementType]. An optional [predicate] can be passed to further filter
     * the elements.
     *
     * @param predicate to filter the elements, defaults to "always true" (aka no filter)
     *
     * @return a new [EntitySet] containing only [GameEntity]s of type [ElementType].
     *
     * @see [filterTyped]
     */
    fun filterElements(predicate: (GameEntity<ElementType>) -> Boolean = { true }): EntitySet<ElementType> =
            filterTyped(predicate)

    /**
     * Shorthand version for `filterElements{ it.currentPosition == location }`
     *
     * @return a new [EntitySet] containing all these entities of type [ElementType] at the given [location].
     */
    fun elementsAt(location: Coordinate): EntitySet<ElementType> =
            filterElements { it.currentPosition == location }

    /**
     * @return The first [GameEntity] of type [ElementType] found at the given [location]
     *  or null if no elements are present.
     */
    fun firstElementAt(location: Coordinate): ElementEntity? =
            filterElements { it.currentPosition == location }.firstOrNull()

    /**
     * Shorthand version for `filterTyped { it.currentPosition in area }`
     *
     * @return all [GameEntity]s that are currently in the given [CoordinateArea]. This means they all need to be
     *  of type [Positionable], so a new [EntitySet] with [EntityType] [Positionable] is returned.
     */
    fun inArea(area: CoordinateArea): EntitySet<Positionable> =
            filterTyped { it.currentPosition in area }

    /**
     * @param entities the entities to exclude
     *
     * @return a new [EntitySet] containing all the entities of this one except [entities].
     */
    fun without(vararg entities: GameEntity<T>): EntitySet<T> =
            allEntities
                    .removeAll(entities.toList())
                    .toEntitySet()

    /**
     * Adds the given [GameEntity] to this [EntitySet] if not yet present.
     */
    fun add(entity: GameEntity<T>) {
        allEntities = allEntities.add(entity)
    }

    /**
     * Removes the given [GameEntity] from this [EntitySet] if possible.
     */
    fun remove(entity: GameEntity<T>) {
        allEntities = allEntities.remove(entity)
    }

    /**
     * Removes all [GameEntity]s from this [EntitySet].
     */
    fun clear() {
        allEntities = allEntities.clear()
    }

    override fun iterator(): Iterator<GameEntity<T>> = allEntities.iterator()

    override fun toString(): String {
        return "EntitySet(${size} entities)"
    }

    override fun contains(element: GameEntity<T>): Boolean =
            allEntities.contains(element)

    override fun containsAll(elements: Collection<GameEntity<T>>): Boolean =
            allEntities.containsAll(elements)

    override fun isEmpty(): Boolean =
            allEntities.isEmpty()
}

/**
 * Creates a new [EntitySet] with the [GameEntity] of this [Sequence].
 */
fun <E : GameEntity<T>, T : EntityType> Sequence<E>.toEntitySet(): EntitySet<T> {
    return EntitySet(this.toSet())
}

/**
 * Creates a new [EntitySet] with the [GameEntity] of this [Iterable].
 */
fun <E : GameEntity<T>, T : EntityType> Iterable<E>.toEntitySet(): EntitySet<T> {
    return EntitySet(this)
}