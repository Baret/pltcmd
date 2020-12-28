package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute

/** The remembered contacts */
class ContactsAttribute : Attribute {
    private val contactsAt: MutableMap<Coordinate, EntitySet<Positionable>> = mutableMapOf()

    private fun getAllAt(key: Coordinate) =
        contactsAt.computeIfAbsent(key) { EntitySet() }

    fun add(entity: PositionableEntity) {
        val key = entity.currentPosition
        getAllAt(key).add(entity)
    }

    fun remove(entity: PositionableEntity) {
        contactsAt.values.forEach { it.remove(entity) }
    }

    // reduce type to Set so the internal EntitySet cannot be modified
    operator fun get(position: Coordinate): Set<PositionableEntity> = getAllAt(position)

    /** @return true if the given entity is known at its current position */
    fun isKnown(entity: PositionableEntity): Boolean = this[entity.currentPosition].contains(entity)

}