package de.gleex.pltcmd.game.application.examples.conversation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.commands.ConversationMessage
import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.inConversationWith
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.fragments.GameTimeFragment
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.events.globalEventBus
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Engine
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.graphics.BoxType
import kotlin.random.Random

/**
 * This little bit of code is used to play around with radio conversations. It creates a GUI where you can
 * manually advance the game ticks to see step by step how the conversations evolve. It currently has
 * 3 [RadioSender]s and the conversations are built before the UI starts. Later it might be possible
 * to dynamically build conversations in the UI.
 */
fun main() {
    val origin = Coordinate(100, 250)
    val tiles = CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT).
            map { coordinate -> WorldTile(coordinate, Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)) }.
            toSortedSet()
    val sector = Sector(origin, tiles)
    val map = WorldMap.create(setOf(sector))
    val game = Game(Engine.create(), map, Random(GameOptions.MAP_SEED))
    val context = game.context()

    globalEventBus.subscribeToBroadcasts { println("RADIO ${Ticker.currentTimeString.value}: ${it.transmission.message}") }

    val hqLocation = origin.toProperty()
    val bravoLocation = origin.withRelativeNorthing(10).toProperty()
    val charlieLocation = origin.withRelativeEasting(10).toProperty()
    val zuluLocation = tiles.last().coordinate.toProperty()

    val hqRadio = RadioSender(hqLocation, RadioPower(50.0), map)
    val bravoRadio = RadioSender(bravoLocation, RadioPower(50.0), map)
    val charlieRadio = RadioSender(charlieLocation, RadioPower(50.0), map)
    val zuluRadio = RadioSender(zuluLocation, RadioPower(1.0), map)

    val hqCallSign = CallSign("Command")
    val bravoCallSign = CallSign("Bravo-2")
    val charlieCallSign = CallSign("Charlie-1")

    val hqEntity = EntityFactory.newElement(Elements.rifleSquad.new().apply { callSign = hqCallSign }, hqLocation, Affiliation.Self, hqRadio)
    val bravoEntity = EntityFactory.newElement(Elements.rifleSquad.new().apply { callSign = bravoCallSign }, bravoLocation, Affiliation.Friendly, bravoRadio)
    val charlieEntity = EntityFactory.newElement(Elements.rifleSquad.new().apply { callSign = charlieCallSign }, charlieLocation, Affiliation.Friendly, charlieRadio)
    val zuluEntity = EntityFactory.newElement(Elements.rifleSquad.new().apply { callSign = CallSign("Zulu-0") }, zuluLocation, Affiliation.Neutral, zuluRadio)

    game.addEntity(hqEntity)
    game.addEntity(bravoEntity)
    game.addEntity(charlieEntity)
    game.addEntity(zuluEntity)

    buildUI(hqEntity, bravoEntity, charlieEntity)

    runBlocking {
        println("creating SITREP from $hqRadio to $bravoRadio")

        hqEntity.sendCommand(ConversationMessage(
                Conversations.Reports.sitrep(
                        sender = hqCallSign,
                        receiver = bravoCallSign
                ),
                context,
                hqEntity
        ))

        println("creating move to from $hqRadio to $charlieRadio")

        hqEntity.sendCommand(ConversationMessage(
                Conversations.Orders.MoveTo.create(
                        sender = hqCallSign,
                        receiver = charlieCallSign,
                        orderLocation = Coordinate(15, 178)
                ),
                context,
                hqEntity
        ))

        println("creating engage from $hqRadio to $bravoRadio")

        hqEntity.sendCommand(ConversationMessage(
                Conversations.Orders.EngageEnemyAt.create(
                        sender = hqCallSign,
                        receiver = bravoCallSign,
                        orderLocation = Coordinate(24, 198)
                ),
                context,
                hqEntity
        ))

        println("creating report position from $bravoRadio to $charlieRadio")

        bravoEntity.sendCommand(ConversationMessage(
                Conversations.Reports.reportPosition(
                        sender = bravoCallSign,
                        receiver = charlieCallSign
                ),
                context,
                bravoEntity
        ))
    }
}

fun buildUI(hqSender: ElementEntity, bravoSender: ElementEntity, charlieSender: ElementEntity) {
    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val screen = application.tileGrid.toScreen()

    screen.themeProperty.value = UiOptions.THEME

    val logAreaHeight = 20
    val logArea = Components.logArea().
    withSize(UiOptions.WINDOW_WIDTH, logAreaHeight).
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
            withSize(UiOptions.WINDOW_WIDTH, UiOptions.WINDOW_HEIGHT - logAreaHeight).
            build().
            apply {
                val hBox = this
                val sideBarWidth = 18
                // sidebar
                addComponent(Components.vbox().
                    withSpacing(1).
                    withSize(sideBarWidth, contentSize.height).
                    build().
                    apply {
                            addFragment(GameTimeFragment(sideBarWidth))
                        })

                // RadioCommunicator panels
                val partSize = Size.create((contentSize.width - sideBarWidth) / 3, contentSize.height)
                addComponent(createRadioCommuicatorPanel(hqSender, partSize))
                addComponent(createRadioCommuicatorPanel(bravoSender, partSize))
                addComponent(createRadioCommuicatorPanel(charlieSender, partSize))
            }

    screen.addComponents(mainPanel, logArea)
    screen.display()
}

fun createRadioCommuicatorPanel(element: ElementEntity, size: Size): Component {
    return Components.vbox().
            withSize(size).
            withSpacing(3).
            withDecorations(ComponentDecorations.box(BoxType.DOUBLE, element.callsign.toString())).
            build().
            apply {
                addComponent(Components.
                        label().
                        withSize(contentSize.width, 1).
                        build().
                        apply {
                            textProperty.updateFrom(
                                    createPropertyFrom("Talking to ")
                                            bindPlusWith element.inConversationWith.bindTransform {
                                        it.map { callSign -> callSign.toString() }
                                                .orElse("nobody")
                                    })
                        })

                // TODO: add list of buffered conversations
                // TODO: Add list of known information
            }
}
