package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

data class TransmissionWithResponse(
        override val message: String,
        val next: Transmission,
        override val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission