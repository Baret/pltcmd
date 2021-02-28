package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.model.elements.Contact
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.world.WorldArea
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val contacts = mutableMapOf<LocatedContact, Visibility>()

    /** current contacts (copy, not a reference) */
    val all: Map<LocatedContact, Visibility>
        // create copy so changes are not visible
        get() = contacts.toMap()

    fun add(contact: LocatedContact, visibility: Visibility) {
        contacts[contact] = visibility
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