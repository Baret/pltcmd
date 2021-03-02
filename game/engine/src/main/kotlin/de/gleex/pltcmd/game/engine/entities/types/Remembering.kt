package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.knowledge.ContactsAttribute
import de.gleex.pltcmd.game.engine.attributes.knowledge.LocatedContact
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import org.hexworks.amethyst.api.entity.EntityType

/**
 * This file contains code for entities that have knowledge attributes.
 */

/** Type marker for entities that have the ContactsAttribute */
interface Remembering : EntityType
typealias RememberingEntity = GameEntity<Remembering>

/** Access to the [ContactsAttribute] of a [RememberingEntity] */
internal val RememberingEntity.contacts: ContactsAttribute
    get() = getAttribute(ContactsAttribute::class)

fun RememberingEntity.addContact(contact: LocatedContact) =
    contacts.add(contact)

fun RememberingEntity.removeContact(contact: LocatedContact) =
    contacts.remove(contact)

fun RememberingEntity.isKnown(contact: LocatedContact) =
    contacts.isKnown(contact)
