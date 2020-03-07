package de.gleex.pltcmd.model.radio.communication.main

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioComms
import de.gleex.pltcmd.model.radio.communication.Sender
import de.gleex.pltcmd.model.radio.communication.TransmissionEvent
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.simpleSubscribeTo

fun main() {
    val bus = EventBus.create()

    bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { println("RADIO: ${it.transmission.message}") }

    val hq = CallSign("Command")
    val receivingCallsign = CallSign("Charlie-1")

    val hqSender = Sender(hq, bus)
    val receiver = Sender(receivingCallsign, bus)
    val notInvolved = Sender(CallSign("Bravo-2"), bus)

    println("testing move to\n\n")

    hqSender.startCommunication(Conversations.moveTo(hq, receivingCallsign, Coordinate(15, 178)))

    println("\n\ntesting report\n\n")

    hqSender.startCommunication(Conversations.reportPosition(hq, receivingCallsign))


    println("sleeping to wait for events")
    println("sleeping")
    Thread.sleep(2000)
    println("awake!")

    bus.close()
}