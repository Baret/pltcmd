package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.model.elements.Contact
import de.gleex.pltcmd.model.world.WorldArea
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val contacts = mutableSetOf<LocatedContact>()

    /** current contacts (copy, not a reference) */
    val all: Set<LocatedContact>
        // create copy so changes are not visible
        get() = contacts.toSet()

    fun add(contact: LocatedContact) {
        contacts.add(contact)
    }

    fun remove(contact: LocatedContact) {
        contacts.remove(contact)
    }

    fun clear() {
        contacts.clear()
    }

    /** @return true if the given contact is known */
    fun isKnown(contact: LocatedContact): Boolean = contacts.contains(contact)

}

data class LocatedContact(val roughLocation: WorldArea, val contact: Contact)