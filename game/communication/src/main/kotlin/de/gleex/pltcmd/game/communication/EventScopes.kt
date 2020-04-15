package de.gleex.pltcmd.game.communication

import org.hexworks.cobalt.events.api.EventScope

/**
 * This scope is used for radio communications. It basically represents the radio network.
 *
 * Events used for this scope are [TransmissionEvent]s.
 *
 * @see [subscribeToRadioComms]
 */
internal object RadioComms : EventScope
