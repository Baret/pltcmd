package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate

class VisualSignal(override val origin: Coordinate, area: WorldArea) : Signal<Vision>(area) {
    override val model: Vision = Vision()
}