package de.gleex.pltcmd.model.radio.communication.transmissions

import kotlin.reflect.KProperty1

data class TerminatingTransmission(
        override val message: String,
        override val contextProperties: Array<out KProperty1<TransmissionContext, Any>> = emptyArray()
): Transmission