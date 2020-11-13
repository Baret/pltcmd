package de.gleex.pltcmd.model.signals.core.builder

import de.gleex.pltcmd.model.signals.core.PropagationModel
import de.gleex.pltcmd.model.signals.core.SignalPower

interface SignalTypeBuilder<P : SignalPower> {
    fun <M: PropagationModel> withPower(power: P): SignalPowerBuilder<M>
}