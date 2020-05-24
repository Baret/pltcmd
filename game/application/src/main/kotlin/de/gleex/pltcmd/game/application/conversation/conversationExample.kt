package de.gleex.pltcmd.game.application.conversation

import de.gleex.pltcmd.game.communication.RadioCommunicator
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.fragments.TickFragment
import de.gleex.pltcmd.game.ui.fragments.TilesetSelectorFragment
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.events.globalEventBus
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
    val origin = Coordinate(100, 250)
    val tiles = CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT).
            map { coordinate -> WorldTile(coordinate, Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)) }.
            toSortedSet()
    val sector = Sector(origin, tiles)
    val map = WorldMap.create(setOf(sector))

    globalEventBus.subscribeToBroadcasts { println("RADIO ${Ticker.currentTimeString.value}: ${it.transmission.message}") }

    val hq = RadioSender(origin, 50.0, map)
    val bravo = RadioSender(origin.withRelativeNorthing(10), 50.0, map)
    val charlie = RadioSender(origin.withRelativeEasting(10), 50.0, map)
    val zulu = RadioSender(tiles.last().coordinate, 1.0, map)

    val hqCallSign = CallSign("Command")
    val bravoCallSign = CallSign("Bravo-2")
    val charlieCallSign = CallSign("Charlie-1")

    val hqSender = RadioCommunicator(hqCallSign, hq)
    val bravoSender = RadioCommunicator(bravoCallSign, bravo)
    val charlieSender = RadioCommunicator(charlieCallSign, charlie)
    // only listens
    RadioCommunicator(CallSign("Zulu-0"), zulu)

    buildUI(hqSender, bravoSender, charlieSender)

    println("creating SITREP from $hq to $bravo")

    hqSender.startCommunication(
            Conversations.Reports.sitrep(
                    sender = hqCallSign,
                    receiver = bravoCallSign
            ))

    println("creating move to from $hq to $charlie")

    hqSender.startCommunication(
            Conversations.Orders.moveTo(
                    sender = hqCallSign,
                    receiver = charlieCallSign,
                    targetLocation = Coordinate(15, 178)
            ))

    println("creating engage from $hq to $bravo")

    hqSender.startCommunication(
            Conversations.Orders.engageEnemyAt(
                    sender = hqCallSign,
                    receiver = bravoCallSign,
                    enemyLocation = Coordinate(24, 198)
            ))

    println("creating report position from $bravo to $charlie")

    bravoSender.startCommunication(
            Conversations.Reports.reportPosition(
                    sender = bravoCallSign,
                    receiver = charlieCallSign
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
        globalEventBus.subscribeToBroadcasts { event ->
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
                                            bindPlusWith communicator.inConversationWith.bindTransform {
                                        it.map { callSign -> callSign.toString() }
                                                .orElse("nobody")
                                    })
                        })

                // TODO: add list of buffered conversations
                // TODO: Add list of known information
            }
}
