package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.model.signals.vision.Visibility
import org.hexworks.amethyst.api.base.BaseAttribute

/** The entities that are currently visible. */
class SightedAttribute : BaseAttribute() {
    private val visibleEntities = mutableMapOf<PositionableEntity, Visibility>()

    val all: Map<PositionableEntity, Visibility>
        // create copy so changes are not visible
        get() = visibleEntities.toMap()

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