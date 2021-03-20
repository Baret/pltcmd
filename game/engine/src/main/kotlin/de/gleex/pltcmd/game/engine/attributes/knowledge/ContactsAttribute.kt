package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.model.elements.Contact
import de.gleex.pltcmd.model.world.WorldArea
import org.hexworks.amethyst.api.base.BaseAttribute

/** The remembered contacts */
class ContactsAttribute : BaseAttribute() {
    private val contacts = mutableMapOf<WorldArea, MutableSet<Contact>>()

    fun add(contact: LocatedContact) {
        contacts.computeIfAbsent(contact.roughLocation) { mutableSetOf() }
            .add(contact.contact)
    }

    fun remove(contact: LocatedContact) {
        contacts.remove(contact)
    }

    fun clear() {
        contacts.clear()
    }

    /** @return true if all details of the given contact are already known, false if it provides additional information */
    fun isKnown(contact: LocatedContact): Boolean {
        return !compareToKnown(contact).isGain
    }

    /** @return how much information is gained from the given contact compared to the already known information. */
    fun compareToKnown(contact: LocatedContact): KnowledgeDifference {
        val matchingContacts = getMatching(contact)
        return when {
            matchingContacts.isEmpty()         -> KnowledgeDifference.NEW
            matchingContacts.contains(contact) -> KnowledgeDifference.SAME
            else                               -> {
                // check if more or less details are known in the matching contacts
                val maxKnownDetails: Int = matchingContacts.map { it.knownDetails() }.maxOrNull() ?: 0
                if (contact.contact.knownDetails() > maxKnownDetails) {
                    KnowledgeDifference.MORE
                } else {
                    KnowledgeDifference.LESS
                }
            }
        }
    }

    fun getMatching(contact: LocatedContact): Set<Contact> =
        contacts[contact.roughLocation]?.getMatching(contact) ?: emptySet()

    companion object {

        fun Collection<Contact>.getMatching(contact: LocatedContact): Set<Contact> {
            val matchers = matchersFor(contact.contact)
            val matchesAll: (Contact) -> Boolean =
                { c ->
                    matchers.fold(true) { result, matcher ->
                        result && matcher(c)
                    }
                }
            return filter(matchesAll)
                .toSet()
        }

        fun Contact.knownDetails(): Int {
            var matches = 0
            val matchers = matchersFor(this)
            matchers.forEach { if (it(this)) matches += 1 }
            return matches
        }

        /** @return list of matches from most generic to finest detail to compare matches */
        fun matchersFor(contact: Contact) = arrayOf<(Contact) -> Boolean>(
            { contact.faction.isEmpty() || it.faction == contact.faction },
            { contact.corps.isEmpty() || it.corps == contact.corps },
            { contact.kind.isEmpty() || it.kind == contact.kind },
            { contact.rung.isEmpty() || it.rung == contact.rung },
            { contact.unitCount.isEmpty() || it.unitCount == contact.unitCount }
        )
    }
}

data class LocatedContact(val roughLocation: WorldArea, val contact: Contact)