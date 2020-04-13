package de.gleex.pltcmd.events

import org.hexworks.cobalt.events.api.EventScope

/**
 * This scope is used for radio communications. It basically represents the radio network.
 *
 * Events used for this scope are [TransmissionEvent]s.
 *
 * @see [EventBus.subscribeToRadioComms]
 */
object RadioComms: EventScope

/**
 *  This scope should only be used by the [de.gleex.pltcmd.events.ticks.Ticker] to publish game ticks and
 *  thus advance game time.
 */
object Ticks: EventScope
