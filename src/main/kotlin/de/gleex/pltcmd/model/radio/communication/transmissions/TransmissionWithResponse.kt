package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext

/**
 * Transmissions of this kind have a follow up transmission.
 */
data class TransmissionWithResponse(
        private val messageTemplate: String,
        val response: Transmission,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
): Transmission(messageTemplate, placeholderValueProviders)