package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

/**
 * A transmission typically ending with "out" (not enforced by this class!) with no following transmissions.
 */
data class TerminatingTransmission(
        private val messageTemplate: String,
        private val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission(messageTemplate, contextProperties)