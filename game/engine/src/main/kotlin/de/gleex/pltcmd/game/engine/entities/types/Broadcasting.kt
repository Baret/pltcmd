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

/** Type marker for entities that have the [RadioAttribute] */
interface Broadcasting : EntityType
typealias BroadcastingEntity = GameEntity<Broadcasting>

val BroadcastingEntity.radio: RadioSender
    get() = getAttribute(RadioAttribute::class).radio

/** @see RadioSender.transmit */
fun BroadcastingEntity.transmit(transmission: Transmission) {
    radio.transmit(transmission)
}
