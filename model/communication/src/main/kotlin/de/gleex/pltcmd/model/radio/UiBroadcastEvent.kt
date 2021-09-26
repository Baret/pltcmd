package de.gleex.pltcmd.model.radio

import kotlinx.serialization.Serializable
import org.hexworks.cobalt.events.api.Event
import org.hexworks.cobalt.events.api.EventScope

@Serializable
data class UiBroadcastEvent(val message: String, val isOpening: Boolean, override val emitter: String) : Event

/**
 * This scope is used for radio broadcasts displayed in the UI.
 */
object UiBroadcasts : EventScope
