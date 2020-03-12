package de.gleex.pltcmd.model.radio.communication.main

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.RadioComms
import de.gleex.pltcmd.events.TransmissionEvent
import de.gleex.pltcmd.events.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.options.UiOptions
import org.hexworks.cobalt.events.api.simpleSubscribeTo
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.graphics.BoxType
import java.util.concurrent.TimeUnit

fun main() {
    val bus = EventBus.instance

    bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { println("RADIO ${Ticker.currentTime()}: ${it.transmission.message}") }

    val hq = CallSign("Command")
    val charlie = CallSign("Charlie-1")
    val bravo = CallSign("Bravo-2")

    val hqSender = RadioCommunicator(hq)
    val charlieSender = RadioCommunicator(charlie)
    val bravoSender = RadioCommunicator(bravo)

    buildUI(hqSender, bravoSender, charlieSender)

    println("creating move to from $hq to $charlie")

    hqSender.startCommunication(
            Conversations.
            moveTo(
                    sender = hq,
                    receiver = charlie,
                    targetLocation = Coordinate(15, 178)
            ))

    println("creating report position from $hq to $charlie")

    hqSender.startCommunication(
            Conversations.
            reportPosition(
                    sender = hq,
                    receiver = charlie
            ))

//    println("creating report position from $bravo to $charlie")
//
//    bravoSender.startCommunication(
//            Conversations.
//            reportPosition(
//                    sender = bravo,
//                    receiver = charlie
//            ))

    repeat(10) {
        Ticker.tick()
        TimeUnit.SECONDS.sleep(2)
    }

    bus.close()
}

fun buildUI(hqSender: RadioCommunicator, bravoSender: RadioCommunicator, charlieSender: RadioCommunicator) {
    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val screen = application.tileGrid.toScreen()

    screen.themeProperty.value = UiOptions.THEME

    val mainPanel = Components.panel().
            withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER).
            withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT - UiOptions.LOG_AREA_HEIGHT).
            build()

    val logArea = Components.logArea().
            withSize(UiOptions.WINDOW_WIDTH, UiOptions.LOG_AREA_HEIGHT).
            withAlignmentWithin(screen, ComponentAlignment.BOTTOM_CENTER).
            withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
            build().
            apply {
                EventBus.instance.simpleSubscribeTo<TransmissionEvent>(RadioComms) { event ->
                    addParagraph("${Ticker.currentTime()}: ${event.transmission.message}", false, 10)
                }
            }

    screen.addComponents(mainPanel, logArea)
    screen.display()
}
