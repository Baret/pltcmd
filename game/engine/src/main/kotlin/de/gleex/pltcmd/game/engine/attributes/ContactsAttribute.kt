package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute

/** The remembered contacts */
class ContactsAttribute : Attribute {
    private val contactsAt: MutableMap<Coordinate, EntitySet<Positionable>> = mutableMapOf()

    private fun getAllAt(key: Coordinate) =
        contactsAt.computeIfAbsent(key) { EntitySet() }

    fun add(entity: PositionableEntity) {
        val key = entity.currentPosition
        val atKey = getAllAt(key)
        // add listener only once!
        if (atKey.contains(entity)) {
            return
        }
        entity.position.onChange { (oldPosition, newPosition, _, _, _, _) ->
            getAllAt(oldPosition).remove(entity)
            getAllAt(newPosition).add(entity)
        }

        atKey.add(entity)
    }

    // reduce type to Set so the internal EntitySet cannot be modified
    operator fun get(position: Coordinate): Set<PositionableEntity> = getAllAt(position)

    fun isKnown(entity: PositionableEntity): Boolean = this[entity.currentPosition].contains(entity)

}