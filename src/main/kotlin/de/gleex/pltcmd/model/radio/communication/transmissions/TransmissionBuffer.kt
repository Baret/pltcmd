package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.events.ticks.TickId
import org.hexworks.cobalt.datatypes.Maybe

class TransmissionBuffer {

    private val buffer = mutableMapOf<TickId, Transmission>()

    fun pop(tickId: TickId): Maybe<Transmission> = Maybe.ofNullable(buffer[tickId])

    fun push(atTick: TickId, transmission: Transmission) {
        var tryingTick = atTick
        do {
            if(buffer.contains(tryingTick)) {
                tryingTick = tryingTick.next
            } else {
                buffer[tryingTick] = transmission
            }
        } while (buffer.contains(tryingTick).not())
    }

}
