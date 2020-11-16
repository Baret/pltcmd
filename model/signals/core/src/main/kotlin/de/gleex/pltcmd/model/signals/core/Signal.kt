package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.math.floor

abstract class Signal<M: PropagationModel<P>, P: SignalPower>(val power: P, val area: WorldArea) {
    abstract val origin: Coordinate

    protected abstract val model: M

    // gibt einen Wert von 0.0 bis 1.0 zurück
    open fun at(target: Coordinate): SignalStrength {
        val terrainList = area[CoordinatePath.line(origin, target)]
                .map { it.terrain }
        return along(terrainList)
    }

    /** @return the SignalStrength at the end of the given terrain and the number of tiles to reach no signal or the end */
    protected fun along(terrain: List<Terrain>): SignalStrength {
        var currentPower: Double = power.initialProcessingValue()
        if(terrain.size <= 1) {
            return model.toSignalStrength(currentPower)
        }
        val startHeight = terrain.first().height // b
        val targetHeight = terrain.last().height
        val slope = (targetHeight.toDouble() - startHeight.toDouble()) / terrain.size.toDouble() // m
        val terrainToTravel = terrain.drop(1)
        for ((index, t) in terrainToTravel.withIndex()) {
            // Calculate if the signal is above, at or through the current field
            // currentHeight (y) = mx + b
            val currentHeight = floor(slope * (index + 1) + startHeight.toDouble())
            currentPower = when {
                // signal travels through the air (above ground)
                currentHeight > t.height.toDouble() -> model.overAir(currentPower)
                // signal travels through the ground
                currentHeight < t.height.toDouble() -> model.throughGround(currentPower)
                // signal travels along the terrain
                else                                -> model.throughTerrain(currentPower, t.type)
            }
            if (currentPower <= model.minThreshold) {
                break
            }
        }
        return model.toSignalStrength(currentPower)
    }

    // evtl hat das noch ein voll imperformantes
//    fun signalMap(): Map<Coordinate: Double> {
//        // das eine Linie zu jedem Punkt in der Reichweite zieht
//        // (so wie das jetzt der SignalVisualizerFragment oder wie das Dingen heisst macht)
//    }
}