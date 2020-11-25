package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe

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
     * @return a new [EntitySet] with entities of type [S]
     */
    inline fun <reified S : EntityType> ofType(): EntitySet<S> {
        return filter { entity -> entity.type is S }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as GameEntity<S>
                }
                .toEntitySet()
    }

    /**
     * @return a new [EntitySet] containing only [GameEntity]s of type [ElementType].
     *
     * @see [ofType]
     */
    fun allElements(): EntitySet<ElementType> = ofType()

    /**
     * @return all entities of type [ElementType] at the given [location].
     */
    fun elementsAt(location: Coordinate): EntitySet<ElementType> =
            allElements()
                    .filter { it.currentPosition == location }
                    .toEntitySet()

    /**
     * @return a [Maybe] containing the first [GameEntity] of type [ElementType] found at the given [location]
     *  or an empty [Maybe] if no elements are present.
     */
    fun firstElementAt(location: Coordinate): Maybe<GameEntity<ElementType>> =
            // This should be faster than using elementsAt.first() because not the whole list needs to be filtered
            Maybe.ofNullable(allElements().firstOrNull { it.currentPosition == location })

    /**
     * @return all [GameEntity]s that are currently in the given [CoordinateArea]. This means they all need to be
     *  of type [Positionable], so a new [EntitySet] with [EntityType] [Positionable] is returned.
     */
    fun inArea(area: CoordinateArea): EntitySet<Positionable> =
            ofType<Positionable>()
                    .filter { it.currentPosition in area }
                    .toEntitySet()

    /**
     * @param entities the entities to exclude
     *
     * @return a new [EntitySet] containing all the entitie of this one except [entities].
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
 * Creates a new [EntitySet] with the [GameEntity] of this [Iterable].
 */
fun <E : GameEntity<T>, T : EntityType> Iterable<E>.toEntitySet(): EntitySet<T> {
    return EntitySet(this)
}
