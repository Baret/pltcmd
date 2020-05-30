package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.amethyst.api.entity.EntityType

/**
 * This file contains code for entities that have the RadioAttribute.
 */

// TODO rename to Radiosomething
/** Type marker for entities that have the [RadioAttribute] */
interface Communicatable : EntityType
typealias CommunicatableEntity = GameEntity<Communicatable>

val CommunicatableEntity.radio: RadioSender
    get() = getAttribute(RadioAttribute::class).radio

/** @see RadioSender.transmit */
fun CommunicatableEntity.transmit(transmission: Transmission) {
    radio.transmit(transmission)
}
