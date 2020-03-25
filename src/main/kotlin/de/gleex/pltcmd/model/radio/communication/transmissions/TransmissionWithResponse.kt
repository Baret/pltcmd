package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

data class TransmissionWithResponse(
        private val messageTemplate: String,
        val next: Transmission,
        private val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission(messageTemplate, contextProperties)