package de.gleex.pltcmd.game.networking

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.BroadcastEvent
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.building.ConversationBuilder
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
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.logging.api.LoggerFactory

private val log = LoggerFactory.getLogger(::startServer::class)

fun startServer(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets)
    routing {
        webSocket("/broadcasts") {
            log.info("sending broadcasts to $logId")

            val eventChannel = Channel<BroadcastEvent>(Channel.BUFFERED)
            // listen to local events
            val subscription = globalEventBus.subscribeToBroadcasts { event ->
                eventChannel.offer(event)
            }
            // clean up on disconnect
            closeReason.invokeOnCompletion {
                log.debug("client closed connection $logId")
                subscription.dispose()
                eventChannel.close()
            }
            // send events to client
            for (event in eventChannel) {
                send(event.toString())
            }

            log.info("finished sending broadcasts to $logId")
        }
    }
}

internal val DefaultWebSocketServerSession.logId: String
    get() {
        val origin = call.request.origin
        return origin.remoteHost
    }

fun main(args: Array<String>) {
    val serverThread = Thread { startServer(args) }
    serverThread.start()
    log.info("creating RadioSender...")
        val testTransmission = ConversationBuilder(
            CallSign("sender"),
            CallSign("receiver")
        ).terminatingResponse("The test finished successfully :)")
        val origin = Coordinate(0, 0)
        val map = dummyMapAt(origin)
        val radioSender = RadioSender(origin.toProperty(), RadioPower.RADIO_POLE, map)
        log.info("done!")
        repeat(3) {
            log.info("sending test")
            radioSender.transmit(testTransmission)
            Thread.sleep(3000)
        }
    log.info("Stopping server...")
    serverThread.stop()
    log.info("Stopped")
}

private fun dummyMapAt(origin: Coordinate) = WorldMap.create(setOf(Sector(
    origin,
    CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
        .map { coordinate -> WorldTile(coordinate, Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)) }
        .toSortedSet()
)))