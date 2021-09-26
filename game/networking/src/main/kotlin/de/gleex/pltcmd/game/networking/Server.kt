package de.gleex.pltcmd.game.networking

import de.gleex.pltcmd.game.engine.entities.EntityFactory
import de.gleex.pltcmd.game.engine.entities.types.CommunicatingEntity
import de.gleex.pltcmd.game.engine.entities.types.communicator
import de.gleex.pltcmd.game.engine.entities.types.onReceivedTransmission
import de.gleex.pltcmd.game.engine.entities.types.onSendTransmission
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.radio.UiBroadcastEvent
import de.gleex.pltcmd.model.radio.communication.building.ConversationBuilder
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.isOpening
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.sender
import de.gleex.pltcmd.model.radio.receivedTransmission
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
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.concurrent.TimeUnit

// there is also an `Application.log` provided by Ktor!
private val logger = LoggerFactory.getLogger(::createServer::class)

internal const val defaultPort = 9170
internal val pathBroadcastEvents = "/broadcasts"

// TODO encapsulate return type. Providing the Ktor implementation class to the caller couples the code to this implementation.
fun createServer(hq: CommunicatingEntity): ApplicationEngine {
    return embeddedServer(Netty, port = defaultPort) {
        install(WebSockets)
        routing {
            broadcastsRoute(hq)
        }
    }
}

private fun Routing.broadcastsRoute(hq: CommunicatingEntity) {
    webSocket(pathBroadcastEvents) {
        logger.info("sending broadcasts to $logId")

        val eventChannel = Channel<UiBroadcastEvent>(Channel.BUFFERED)
        // listen to local events
        val subscriptionReceived = hq.onReceivedTransmission(eventChannel::trySendLogging)
        val subscriptionSend = hq.onSendTransmission(eventChannel::trySendLogging)
        // clean up on disconnect
        closeReason.invokeOnCompletion {
            logger.debug("client closed connection $logId")
            subscriptionReceived.dispose()
            subscriptionSend.dispose()
            eventChannel.close()
        }
        // send events to client
        for (event in eventChannel) {
            // second send over network
            val bytes = ProtoBuf.encodeToByteArray(event)
            send(bytes)
        }

        logger.info("finished sending broadcasts to $logId")
    }
}

/** sends the given Event to the given channel and logs the result. */
private fun Channel<UiBroadcastEvent>.trySendLogging(event: Transmission) {
    // first convert
    val uiEvent = event.uiEvent
    // and queue event
    val sendResult = trySend(uiEvent)
    if (sendResult.isFailure) {
        logger.error("failed to queue event $uiEvent for network transmission due to ${sendResult.exceptionOrNull()}")
    } else if (sendResult.isSuccess) {
        logger.trace("successfully queued event $uiEvent for network transmission")
    }
}

// TODO move somewhere else
val Transmission.uiEvent: UiBroadcastEvent
    get() {
        val message = "${Ticker.currentTimeString.value}: ${message}"
        val senderName = sender.name
        return UiBroadcastEvent(message, isOpening, senderName)
    }

internal val DefaultWebSocketServerSession.logId: String
    get() {
        val origin = call.request.origin
        return origin.remoteHost
    }

fun main(args: Array<String>) {
    // setup communication model
    logger.info("creating HQ...")
    val sender = CallSign("sender")
    val testTransmission = ConversationBuilder(
        sender,
        CallSign("receiver")
    ).terminatingResponse("The test finished successfully :)")
    val origin = Coordinate(0, 0)
    val map = dummyMapAt(origin)
    val hq: CommunicatingEntity = EntityFactory.newBaseAt(origin, map, Faction("example"), sender)
    logger.info("done!")

    // start networking
    val serverThread = createServer(hq)
    serverThread.start(wait = false)

    // transfer data
    repeat(3) {
        logger.info("sending test")
        globalEventBus.receivedTransmission(hq.communicator, testTransmission)
        Thread.sleep(3000)
    }

    // done
    logger.info("Stopping server...")
    serverThread.stop(200, 500, TimeUnit.MILLISECONDS)
    logger.info("Stopped")
}

private fun dummyMapAt(origin: Coordinate) = WorldMap.create(setOf(Sector(
    origin,
    CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
        .map { coordinate -> WorldTile(coordinate, Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)) }
        .toSortedSet()
)))