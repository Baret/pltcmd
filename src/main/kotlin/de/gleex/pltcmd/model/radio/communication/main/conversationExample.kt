package de.gleex.pltcmd.model.radio.communication.main

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.RadioComms
import de.gleex.pltcmd.events.TransmissionEvent
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.events.api.simpleSubscribeTo
import java.util.concurrent.TimeUnit

fun main() {
    val bus = EventBus.instance

    bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { println("RADIO: ${it.transmission.message}") }

    val hq = CallSign("Command")
    val receivingCallsign = CallSign("Charlie-1")

    val hqSender = RadioCommunicator(hq)
    val receiver = RadioCommunicator(receivingCallsign)
    val notInvolved = RadioCommunicator(CallSign("Bravo-2"))

    println("testing move to\n\n")

    hqSender.startCommunication(
            Conversations.
            moveTo(
                    sender = hq,
                    receiver = receivingCallsign,
                    targetLocation = Coordinate(15, 178)
            ))

    println("\n\ntesting report\n\n")

    hqSender.startCommunication(
            Conversations.
            reportPosition(
                    sender = hq,
                    receiver = receivingCallsign
            ))

    println("sleeping to wait for events")
    println("sleeping")
    TimeUnit.SECONDS.sleep(2)
    println("awake!")

    bus.close()
}