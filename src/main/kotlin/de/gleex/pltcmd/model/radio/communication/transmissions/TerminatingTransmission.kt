package de.gleex.pltcmd.model.radio.communication.transmissions

/**
 * A transmission typically ending with "out" (not enforced by this class!) with no following transmissions.
 */
data class TerminatingTransmission(
        private val messageTemplate: String,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
): Transmission(messageTemplate, placeholderValueProviders)