package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.ContactsAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldArea

/**
 * This file contains code for entities that have the [VisionAttribute] and [ContactsAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
/**
 * An entity of type [Seeing]
 */
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visionAttribute: VisionAttribute
    get() = getAttribute(VisionAttribute::class)

internal var SeeingEntity.visionMutable: Vision
    get() = visionAttribute.vision
    set(value) {
        visionAttribute.vision = value
    }

/**
 * The [Vision] of this entity.
 */
// visible for the UI
val SeeingEntity.vision: Vision
    get() = visionMutable

internal val SeeingEntity.visibleTiles: WorldArea
    get() = vision.area

internal val SeeingEntity.visualRange: VisionPower
    get() = visionAttribute.visualRange

////// ContactsAttribute
private val SeeingEntity.contacts: ContactsAttribute
    get() = getAttribute(ContactsAttribute::class)

internal infix fun SeeingEntity.hasNewContact(toReport: PositionableEntity) = !knowsContact(toReport)

internal infix fun SeeingEntity.knowsContact(entity: PositionableEntity): Boolean {
    return contacts.isKnown(entity)
}

/** @return true if the contact is new or false if is already known */
internal infix fun SeeingEntity.rememberContact(entity: PositionableEntity): Boolean {
    if (this knowsContact entity) {
        return false
    }
    forgetContact(entity)
    contacts.add(entity)
    return true
}

internal infix fun SeeingEntity.forgetContact(entity: PositionableEntity) {
    return contacts.remove(entity)
}
