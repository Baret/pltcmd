package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.reflect.KClass

class EntitySet<T : EntityType>(initialSet: Set<GameEntity<T>> = emptySet()) : Set<GameEntity<T>> {

    private var allEntities: PersistentSet<GameEntity<T>> = initialSet.toPersistentSet()

    override val size: Int
        get() = allEntities.size

    inline fun <reified T : EntityType> ofType(type: KClass<T>): EntitySet<T> {
        return filter { entity -> type.isInstance(entity.type) }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as GameEntity<T>
                }
                .toEntitySet()
    }

    fun allElements(): EntitySet<ElementType> = ofType(ElementType::class)

    fun inArea(area: CoordinateArea): EntitySet<Positionable> =
            ofType(Positionable::class)
                    .filter { it.currentPosition in area }
                    .toEntitySet()

    fun without(vararg entities: GameEntity<T>): EntitySet<T> =
            allEntities.removeAll(entities.toList())
                    .toEntitySet()

    fun add(entity: GameEntity<T>) {
        allEntities = allEntities.add(entity)
    }

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

fun <E : GameEntity<T>, T : EntityType> Iterable<E>.toEntitySet(): EntitySet<T> {
    return EntitySet(toSet())
}
