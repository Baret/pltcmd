package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.model.signals.vision.Visibility
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val visibleEntities = mutableMapOf<PositionableEntity, Visibility>()

    fun getAll(): Map<PositionableEntity, Visibility> {
        // create copy so changes are not visible
        return visibleEntities.toMap()
    }

    fun add(entity: PositionableEntity, visibility: Visibility) {
        visibleEntities[entity] = visibility
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