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

internal const val defaultPort = 9170

fun connect(host: String = "127.0.0.1", port: Int = defaultPort) {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = defaultPort, path = pathBroadcastEvents) {
            repeat(2) {
                val frame = incoming.receive() as Frame.Binary
                val bytes = frame.readBytes()
                val broadcastEvent = ProtoBuf.decodeFromByteArray<UiBroadcastEvent>(bytes)
                println(broadcastEvent)
                uiEventBus.publish(broadcastEvent, UiBroadcasts)
            }
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}

fun main() {
    connect()
}
