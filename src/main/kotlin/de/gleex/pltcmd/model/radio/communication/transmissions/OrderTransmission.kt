package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

data class OrderTransmission(
        override val message: String,
        val positiveAwnser: Transmission,
        val negativeAwnser: Transmission,
        override val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission