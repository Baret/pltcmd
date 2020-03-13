package de.gleex.pltcmd.model.radio.communication.main

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.fragments.TickFragment
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.graphics.BoxType

fun main() {
    EventBus.subscribeToRadioComms { println("RADIO ${Ticker.currentTime()}: ${it.transmission.message}") }

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

    println("creating engage from $hq to $bravo")

    hqSender.startCommunication(
            Conversations.
            engageEnemyAt(
                    sender = hq,
                    receiver = bravo,
                    enemyLocation = Coordinate(24, 198)
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
                addFragment(TickFragment(sideBarWidth))

                // RadioCommunicator panels
                val partSize = Size.create((contentSize.width - sideBarWidth) / 3, contentSize.height)
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
                EventBus.subscribeToRadioComms { event ->
                    addParagraph("${Ticker.currentTime()}: ${event.transmission.message}", false, 10)
                }
            }

    screen.addComponents(mainPanel, logArea)
    screen.display()
}

fun ceateRadioCommuicatorPanel(communicator: RadioCommunicator, size: Size): Component {
    return Components.vbox().
            withSize(size).
            withSpacing(3).
            withDecorations(ComponentDecorations.box(BoxType.DOUBLE, communicator.callSign.toString())).
            build().
            apply {
                val conversationPartner = createPropertyFrom(communicator.conversationActiveWith)
                EventBus.subscribeToTicks { conversationPartner.value = communicator.conversationActiveWith }
                addComponent(Components.
                        label().
                        withSize(contentSize.width, 1).
                        build().
                        apply {
                            textProperty.updateFrom(
                                    createPropertyFrom("Talking to ") bindPlusWith conversationPartner.bindTransform { it.map { it.toString() }.orElse("nobody") })
                        })

                // TODO: add list of buffered conversations
            }
}
