package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

/**
 * Transmissions of this kind have a follow up transmission.
 */
data class TransmissionWithResponse(
        private val messageTemplate: String,
        val response: Transmission,
        private val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission(messageTemplate, contextProperties)