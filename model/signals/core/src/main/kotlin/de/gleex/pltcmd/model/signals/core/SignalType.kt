package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.signals.core.builder.SignalTypeBuilder
import de.gleex.pltcmd.model.world.WorldMap

abstract class SignalType() {
    abstract fun <P: SignalPower> newBuilder(worldMap: WorldMap): SignalTypeBuilder<P>
}
