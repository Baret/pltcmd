package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.communication.RadioCommunicator
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.entity.EntityType

/**
 * This file contains code for entities that have the RadioAttribute.
 */

/** Type marker for entities that have the [RadioAttribute] */
interface Communicatable : EntityType
typealias CommunicatableEntity = GameEntity<Communicatable>

private val CommunicatableEntity.communicator: RadioCommunicator
    get() = getAttribute(RadioAttribute::class).communicator

val CommunicatableEntity.callSign: CallSign
        get() = communicator.callSign

/** @see RadioCommunicator.startCommunication */
fun CommunicatableEntity.startConversation(conversation: Conversation) {
    require(callSign == conversation.sender)
    communicator.startCommunication(conversation)
}
