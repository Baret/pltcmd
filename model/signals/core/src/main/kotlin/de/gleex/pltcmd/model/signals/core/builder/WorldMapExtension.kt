package de.gleex.pltcmd.model.signals.core.builder

import de.gleex.pltcmd.model.signals.core.SignalPower
import de.gleex.pltcmd.model.signals.core.SignalType
import de.gleex.pltcmd.model.world.WorldMap

fun <P: SignalPower> WorldMap.signal(type: SignalType): SignalTypeBuilder<P> {
    return type.newBuilder(this)
}