package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.toEntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.model.signals.vision.Visibility
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val visibleEntities = mutableMapOf<PositionableEntity, Visibility>()

    fun getAll(): EntitySet<Positionable> {
        // create copy so changes are not visible
        return visibleEntities.keys.toEntitySet()
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