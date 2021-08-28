package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.*
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.BroadcastEvent
import de.gleex.pltcmd.model.radio.TransmissionReceivedEvent
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.radio.subscribeToReceivedTransmissions
import de.gleex.pltcmd.model.signals.radio.RadioSignal
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * This file contains code for entities that have the RadioAttribute.
 */

/** Type marker for entities that have the [RadioAttribute] */
interface Communicating : Factionable
typealias CommunicatingEntity = GameEntity<Communicating>

private val log = LoggerFactory.getLogger(Communicating::class)

internal val CommunicatingEntity.communicator: RadioCommunicator
    get() = getAttribute(RadioAttribute::class).communicator

/**
 * The current [RadioSignal] of this entity.
 */
val CommunicatingEntity.radioSignal: RadioSignal
    get() = communicator.currentSignal

/** TODO is visible as a debug feature for the test UI, might be removed later */
val CommunicatingEntity.inConversationWith: ObservableValue<Maybe<CallSign>>
    get() = communicator.inConversationWith

/** True if this entity is currently in a conversation */
val CommunicatingEntity.isTransmitting
    get() = communicator.inConversationWith.value.isPresent

/** The name under which this entity identifies itself when communicating by radio */
val CommunicatingEntity.radioCallSign: CallSign
    get() = communicator.callSign

/** Whenever this entity receives a [Transmission] let the given handler process it. */
fun CommunicatingEntity.onReceivedTransmission(handler: (Transmission) -> Unit): Subscription {
    return globalEventBus.subscribeToReceivedTransmissions(communicator) { event: TransmissionReceivedEvent ->
        handler(event.transmission)
    }
}

/** Whenever this entity sends a [Transmission] let the given handler process it. */
fun CommunicatingEntity.onSendTransmission(handler: (Transmission) -> Unit): Subscription {
    return globalEventBus.subscribeToBroadcasts { event: BroadcastEvent ->
        if (!communicator.isSender(event.emitter)) {
            return@subscribeToBroadcasts
        }
        handler(event.transmission)
    }
}

/**
 * Queues the given conversation.
 */
// TODO: Instantly start the conversation if possible. Currently it takes 2 ticks until the conversation actually starts
internal fun CommunicatingEntity.startConversation(conversation: Conversation) {
    log.debug("$logIdentifier starting conversation $conversation")
    communicator.startConversation(conversation)
}

/**
 * Invokes [whenCommunicating] if this entity is a [CommunicatingEntity]. When the type is not [Communicating],
 * [whenOther] is invoked instead.
 *
 * @param R the type that is returned by [whenCommunicating] or [whenOther]
 */
fun <R> AnyGameEntity.asCommunicatingEntity(whenCommunicating: (CommunicatingEntity) -> R): Maybe<R> =
    tryCastTo<CommunicatingEntity, Communicating, R>(whenCommunicating)
