package de.gleex.pltcmd.model.signals.core.builder

import de.gleex.pltcmd.model.signals.core.PropagationModel
import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.world.coordinate.Coordinate

interface SignalPowerBuilder<M: PropagationModel> {
    fun at(origin: Coordinate): Signal<M>
}
