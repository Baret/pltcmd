package de.gleex.pltcmd.util.events

import org.hexworks.cobalt.events.api.EventBus

/**
 * Instance for all events.
 */
val globalEventBus = EventBus.create()

/**
 * Instance for client events.
 */
val uiEventBus = EventBus.create()
