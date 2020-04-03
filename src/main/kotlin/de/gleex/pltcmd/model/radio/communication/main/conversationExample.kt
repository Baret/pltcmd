package de.gleex.pltcmd.model.radio.communication.main

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.fragments.TilesetSelectorFragment
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

/**
 * This little bit of code is used to play around with radio conversations. It creates a GUI where you can
 * manually advance the game ticks to see step by step how the conversations evolve. It currently has
 * 3 [RadioCommunicator]s and the conversations are built before the UI starts. Later it might be possible
 * to dynamically build conversations in the UI.
 */
fun main() {
    EventBus.subscribeToRadioComms { println("RADIO ${Ticker.currentTimeString.value}: ${it.transmission.message}") }

    val hq = CallSign("Command")
    val charlie = CallSign("Charlie-1")
    val bravo = CallSign("Bravo-2")

    val hqSender = RadioCommunicator(hq)
    val charlieSender = RadioCommunicator(charlie)
    val bravoSender = RadioCommunicator(bravo)

    buildUI(hqSender, bravoSender, charlieSender)

    println("creating SITREP from $hq to $bravo")

    hqSender.startCommunication(
            Conversations.Reports.
            sitrep(
                    sender = hq,
                    receiver = bravo
            ))

    println("creating move to from $hq to $charlie")

    hqSender.startCommunication(
            Conversations.Orders.
            moveTo(
                    sender = hq,
                    receiver = charlie,
                    targetLocation = Coordinate(15, 178)
            ))

    println("creating engage from $hq to $bravo")

    hqSender.startCommunication(
            Conversations.Orders.
            engageEnemyAt(
                    sender = hq,
                    receiver = bravo,
                    enemyLocation = Coordinate(24, 198)
            ))

    println("creating report position from $bravo to $charlie")

    bravoSender.startCommunication(
            Conversations.Reports.
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
    val logArea = Components.logArea().
    withSize(UiOptions.WINDOW_WIDTH, LOG_AREA_HEIGHT).
    withAlignmentWithin(screen, ComponentAlignment.BOTTOM_CENTER).
    withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
    build().
    apply {
        EventBus.subscribeToRadioComms { event ->
            addParagraph("${Ticker.currentTimeString.value}: ${event.transmission.message}", false, 10)
        }
    }

    val mainPanel = Components.hbox().
            withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER).
            withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT - LOG_AREA_HEIGHT).
            build().
            apply {
                val sideBarWidth = 18
                // sidebar
                addComponent(Components.vbox().
                    withSpacing(1).
                    withSize(sideBarWidth, contentSize.height).
                    build().
                    apply {
                        addFragment(TickFragment(sideBarWidth))
                        // TESTING
                        addFragment(TilesetSelectorFragment(sideBarWidth, this@apply, logArea))
                    })

                // RadioCommunicator panels
                val partSize = Size.create((contentSize.width - sideBarWidth) / 3, contentSize.height)
                addComponent(ceateRadioCommuicatorPanel(hqSender, partSize))
                addComponent(ceateRadioCommuicatorPanel(bravoSender, partSize))
                addComponent(ceateRadioCommuicatorPanel(charlieSender, partSize))
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
                addComponent(Components.
                        label().
                        withSize(contentSize.width, 1).
                        build().
                        apply {
                            textProperty.updateFrom(
                                    createPropertyFrom("Talking to ")
                                                bindPlusWith communicator.inConversationWith.
                                                    bindTransform { it.map { callSign -> callSign.toString() }.orElse("nobody") })
                        })

                // TODO: add list of buffered conversations
                // TODO: Add list of known information
            }
}
