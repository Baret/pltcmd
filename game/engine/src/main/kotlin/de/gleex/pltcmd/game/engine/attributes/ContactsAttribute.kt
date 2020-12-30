package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import org.hexworks.amethyst.api.Attribute

/** The remembered contacts */
class ContactsAttribute : Attribute {
    private val visibleEntities: EntitySet<Positionable> = EntitySet()

    fun getAll(): Set<PositionableEntity> {
        // create copy so changes are not visible
        return visibleEntities.toSet()
    }

    fun add(entity: PositionableEntity) {
        visibleEntities.add(entity)
    }

    fun remove(entity: PositionableEntity) {
        visibleEntities.remove(entity)
    }

    fun clear() {
        visibleEntities.clear()
    }

    /** @return true if the given entity is currently visible */
    fun isVisible(entity: PositionableEntity): Boolean = visibleEntities.contains(entity)

}