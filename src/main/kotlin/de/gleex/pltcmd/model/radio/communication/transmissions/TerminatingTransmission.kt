package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

data class TerminatingTransmission(
        private val messageTemplate: String,
        private val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission(messageTemplate, contextProperties)