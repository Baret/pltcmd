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
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.events.api.simpleSubscribeTo
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.uievent.ComponentEventType

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

    println("creating report position from $bravo to $charlie")

    bravoSender.startCommunication(
            Conversations.
            reportPosition(
                    sender = bravo,
                    receiver = charlie
            ))
}

fun buildUI(hqSender: RadioCommunicator, bravoSender: RadioCommunicator, charlieSender: RadioCommunicator) {
    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val screen = application.tileGrid.toScreen()

    screen.themeProperty.value = UiOptions.THEME

    val LOG_AREA_HEIGHT = 20
    val mainPanel = Components.hbox().
            withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER).
            withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT - LOG_AREA_HEIGHT).
            build().
            apply {
                val sideBarWidth = 18
                // sidebar
                addComponent(Components.
                        vbox().
                        withSize(sideBarWidth, contentSize.height).
                        withDecorations(ComponentDecorations.box(BoxType.DOUBLE)).
                        build().
                        apply {
                            addComponent(Components.header().withText("Current Tick"))
                            addComponent(Components.label().
                                withSize(contentSize.width, 1).
                                build().
                                apply {
                                    textProperty.updateFrom(
                                            Ticker.currentTickProperty.bindTransform { it.toString() }
                                                    bindPlusWith createPropertyFrom(": ")
                                                    bindPlusWith Ticker.currentTimeStringProperty)
                                }
                            )
                            addComponent(Components.button().
                                withText("TICK!").
                                build().
                                apply {
                                    processComponentEvents(ComponentEventType.ACTIVATED) { Ticker.tick() }
                                })
                        }
                    )

                // RadioCommunicator panels
                val partSize = Size.create((contentSize.width - sideBarWidth) / 3, contentSize.height)
                println("contentsize of main panel = $contentSize")
                println("partSize = $partSize")
                addComponent(ceateRadioCommuicatorPanel(hqSender, partSize))
                addComponent(ceateRadioCommuicatorPanel(bravoSender, partSize))
                addComponent(ceateRadioCommuicatorPanel(charlieSender, partSize))
            }

    val logArea = Components.logArea().
            withSize(UiOptions.WINDOW_WIDTH, LOG_AREA_HEIGHT).
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

fun ceateRadioCommuicatorPanel(communicator: RadioCommunicator, size: Size): Component {
    return Components.vbox().
            withSize(size).
            withDecorations(ComponentDecorations.box(BoxType.DOUBLE, communicator.callSign.toString())).
            build().
            apply {
                addComponent(Components.header().withText(communicator.callSign.toString()))
            }
}
