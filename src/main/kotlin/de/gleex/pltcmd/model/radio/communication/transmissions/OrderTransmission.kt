package de.gleex.pltcmd.model.radio.communication.transmissions

/**
 * An order has two answers, a positive and a negative one.
 */
data class OrderTransmission(
        private val messageTemplate: String,
        val positiveAnswer: Transmission,
        val negativeAnswer: Transmission,
        private val contextLambda: TransmissionContext.() -> Array<out Any?> = {emptyArray()}
): Transmission(messageTemplate, contextLambda)