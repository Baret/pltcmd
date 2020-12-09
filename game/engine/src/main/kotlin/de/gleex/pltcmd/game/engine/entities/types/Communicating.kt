package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.signals.radio.RadioSignal
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * This file contains code for entities that have the RadioAttribute.
 */

/** Type marker for entities that have the [RadioAttribute] */
interface Communicating : EntityType
typealias CommunicatingEntity = GameEntity<Communicating>

private val log = LoggerFactory.getLogger(Communicating::class)

internal val CommunicatingEntity.communicator: RadioCommunicator
    get() = getAttribute(RadioAttribute::class).communicator

/**
 * The [RadioPower] of this entity.
 */
val CommunicatingEntity.radioSignal: RadioSignal
    get() = communicator.currentSignal

/** TODO is visible as a debug feature for the test UI, might be removed later */
val CommunicatingEntity.inConversationWith: ObservableValue<Maybe<CallSign>>
    get() = communicator.inConversationWith

/** True if this entity is currently in a conversation */
val CommunicatingEntity.isTransmitting
    get() = communicator.inConversationWith.value.isPresent

/**
 * Queues the given conversation.
 */
// TODO: Instantly start the conversation if possible. Currently it takes 2 ticks until the conversation actually starts
internal fun CommunicatingEntity.startConversation(conversation: Conversation) {
    log.debug("${(this as ElementEntity).callsign} starting conversation $conversation")
    communicator.startConversation(conversation)
}
