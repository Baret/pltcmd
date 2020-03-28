package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.events.ticks.TickId
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This buffer is used to queue [Transmission]s that need to be sent at specific ticks. Use [push] to
 * remember a transmission for an upcoming tick and [pop] to see if there is a transmission for the given tick.
 */
class TransmissionBuffer {

    private val buffer = mutableMapOf<TickId, Transmission>()

    fun pop(tickId: TickId): Maybe<Transmission> = Maybe.ofNullable(buffer[tickId])

    /**
     * Pushes the given transmission into the buffer associated with the given [TickId]. There can always only
     * be one transmission per tick! When a transmission is already present for the given tick it is pushed to the
     * next "free" tick.
     */
    fun push(atTick: TickId, transmission: Transmission) {
        var tryingTick = atTick
        while (buffer.contains(tryingTick)) {
            tryingTick = tryingTick.next
        }
        buffer[tryingTick] = transmission
    }

}
