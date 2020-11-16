package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.reflect.KClass

class EntitySet<T : EntityType>(val all: Set<GameEntity<T>> = emptySet()) : Set<GameEntity<T>> by all {
    private val allEntities: MutableSet<GameEntity<T>> = all.toMutableSet()

    inline fun <reified T : EntityType> ofType(type: KClass<T>): EntitySet<T> {
        return all
                .filter { type.isInstance(it) }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as GameEntity<T>
                }
                .toEntitySet()
    }

    fun allElements(): EntitySet<ElementType> = ofType(ElementType::class)

    fun add(entity: GameEntity<T>) {
        allEntities.add(entity)
    }

    fun remove(entity: GameEntity<T>) {
        allEntities.remove(entity)
    }

    override fun iterator(): Iterator<GameEntity<T>> = allEntities.iterator()

    fun inArea(area: CoordinateArea): EntitySet<Positionable> =
            ofType(Positionable::class)
                    .filter { it.currentPosition in area }
                    .toEntitySet()
}

fun <E : GameEntity<T>, T : EntityType> Iterable<E>.toEntitySet(): EntitySet<T> {
    return EntitySet(toSet())
}
