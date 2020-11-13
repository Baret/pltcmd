package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate

abstract class Signal<M: PropagationModel>(val area: WorldArea) {
    abstract val origin: Coordinate

    protected abstract val model: M

    // gibt einen Wert von 0.0 bis 1.0 zurück
    fun to(target: Coordinate): Double {
        TODO("Move radio signal code over to Signal")
    }

    // evtl hat das noch ein voll imperformantes
//    fun signalMap(): Map<Coordinate: Double> {
//        // das eine Linie zu jedem Punkt in der Reichweite zieht
//        // (so wie das jetzt der SignalVisualizerFragment oder wie das Dingen heisst macht)
//    }
}