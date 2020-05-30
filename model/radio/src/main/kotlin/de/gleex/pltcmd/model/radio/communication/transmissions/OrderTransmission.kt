package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext

/**
 * An order has two answers, a positive and a negative one.
 */
data class OrderTransmission(
        internal val messageTemplate: String,
        val positiveAnswer: Transmission,
        val negativeAnswer: Transmission,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
): Transmission(messageTemplate, placeholderValueProviders)