package de.gleex.pltcmd.game.networking

import de.gleex.pltcmd.model.radio.UiBroadcastEvent
import de.gleex.pltcmd.model.radio.UiBroadcasts
import de.gleex.pltcmd.util.events.uiEventBus
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hexworks.cobalt.logging.api.LoggerFactory

internal const val defaultPort = 9170

private val log = LoggerFactory.getLogger(::connect::class)

fun connect(host: String = "127.0.0.1", port: Int = defaultPort) {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        log.info("Connecting to server $host:$port")
        client.webSocket(method = HttpMethod.Get, host = host, port = port, path = pathBroadcastEvents) {
            for (frame in incoming) {
                val bytes = frame.readBytes()
                val broadcastEvent = ProtoBuf.decodeFromByteArray<UiBroadcastEvent>(bytes)
                log.trace{"received event $broadcastEvent"}
                uiEventBus.publish(broadcastEvent, UiBroadcasts)
            }
        }
    }
    client.close()
    log.info("Connection to server closed.")
}

fun main() {
    connect()
}
