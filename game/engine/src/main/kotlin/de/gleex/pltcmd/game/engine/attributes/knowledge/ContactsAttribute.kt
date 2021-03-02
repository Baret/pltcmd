package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.model.elements.Contact
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.util.collections.filterUntilFound
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val contacts = mutableMapOf<WorldArea, MutableSet<Contact>>()

    fun add(contact: LocatedContact): KnowledgeDifference {
        val previousMatches = getMatching(contact)
        val isNew = contacts.computeIfAbsent(contact.roughLocation) { mutableSetOf<Contact>() }
            .add(contact.contact)
        return when {
            isNew                             -> KnowledgeDifference.MORE
            previousMatches.contains(contact) -> KnowledgeDifference.SAME
            else                              -> {
                // TODO use partial comparison to find match
                KnowledgeDifference.LESS
            }
        }
    }

    fun remove(contact: LocatedContact) {
        contacts.remove(contact)
    }

    fun clear() {
        contacts.clear()
    }

    /** @return true if the given contact is known at that location */
    fun isKnown(contact: LocatedContact): Boolean =
        contacts[contact.roughLocation]?.contains(contact.contact) ?: false

    fun getMatching(contact: LocatedContact): Set<Contact> =
        contacts[contact.roughLocation]?.getMatching(contact) ?: emptySet()

    fun Collection<Contact>.getMatching(contact: LocatedContact): Set<Contact> {
        return filterUntilFound({ it.faction == contact.contact.faction },
            { it.corps == contact.contact.corps },
            { it.kind == contact.contact.kind },
            { it.kind == contact.contact.rung },
            { it.kind == contact.contact.unitCount })
    }
}

data class LocatedContact(val roughLocation: WorldArea, val contact: Contact)