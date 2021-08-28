package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.*

/**
 * A [Transmission] that was received by a [RadioCommunicator].
 */
data class TransmissionReceivedEvent(
    override val emitter: RadioCommunicator,
    /**
     * The [Transmission] that was received.
     */
    val transmission: Transmission
) : Event {
    override val key: String
        get() = emitter.eventKey
}

/**
 * This scope is used for received transmissions. It basically represents a log of messages.
 */
private object Transmissions : EventScope

/**
 * Method to subscribe to [TransmissionReceivedEvent]s
 *
 * @param byRadio only transmissions received by this radio will be given to [onEvent]
 * @param onEvent code that will receive each transmission of [byRadio]
 * @return a [Subscription] that can be disposed to stop handling events
 */
fun EventBus.subscribeToReceivedTransmissions(
    byRadio: RadioCommunicator,
    onEvent: (TransmissionReceivedEvent) -> Unit
): Subscription {
    return subscribeTo<TransmissionReceivedEvent>(Transmissions, byRadio.eventKey) {
        onEvent(it)
        KeepSubscription
    }
}

/**
 * Publishes a [TransmissionReceivedEvent].
 *
 * @param receiver the [RadioCommunicator] that received the transmission
 * @param transmission the received [Transmission]
 */
internal fun EventBus.receivedTransmission(receiver: RadioCommunicator, transmission: Transmission) =
    publish(TransmissionReceivedEvent(receiver, transmission), Transmissions)

/** Returns a key that uniquely identifies this receiving object */
private val RadioCommunicator.eventKey: String
    get() = toString()
