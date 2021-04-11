package de.gleex.pltcmd.game.networking

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking

internal val defaultPort = 9170

fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = defaultPort, path = pathBroadcastEvents) {
            repeat(2) {
                val broadcastEvent = incoming.receive() as? Frame.Text
                println(broadcastEvent?.readText())
            }
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}