package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

/**
 * An order has two answers, a positive and a negative one.
 */
data class OrderTransmission(
        private val messageTemplate: String,
        val positiveAwnser: Transmission,
        val negativeAwnser: Transmission,
        private val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission(messageTemplate, contextProperties)