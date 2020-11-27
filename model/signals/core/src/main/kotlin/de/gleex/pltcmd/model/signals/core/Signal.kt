package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.math.floor

abstract class Signal<M : PropagationModel<P>, P : SignalPower>(val power: P, val area: WorldArea) {
    abstract val origin: Coordinate

    private val signalCache: MutableMap<Coordinate, SignalStrength> = mutableMapOf()

    /**
     * All [Coordinate]s of [area] mapped to their corresponding [SignalStrength]. The same as if
     * you would call [at] for every single coordinate.
     */
    val signalMap: Map<Coordinate, SignalStrength>
        // Calculate or load signals from cache for every coordinate in the area
        get() = area.associateWith { coordinate -> at(coordinate) }

    protected abstract val model: M

    abstract val maxRangeInTiles: Int

    /**
     *
     */
    fun at(target: Coordinate): SignalStrength {
        return signalCache.computeIfAbsent(target) { calculateSignalStrengthAt(it) }
    }

    /**
     * Used to calculate the [SignalStrength] at the given coordinate. By default it creates a straight line
     * from [origin] to [target] and calls [along]. This behavior may be overridden though.
     */
    protected open fun calculateSignalStrengthAt(target: Coordinate): SignalStrength {
        val terrainList = area[CoordinatePath.line(origin, target)]
                .map { it.terrain }
        return along(terrainList)
    }

    /**
     * Calculates signal loss along the given terrain which is usually a straight line.
     *
     * @param terrainLine the line of terrain that the signal travels along **including** the origin
     * @return the [SignalStrength] at the end of the given terrain
     */
    protected fun along(terrainLine: List<Terrain>): SignalStrength {
        var currentPower: Double = power.initialProcessingValue()
        if (terrainLine.size > 1) {
            val startHeight = terrainLine.first().height // b
            val targetHeight = terrainLine.last().height
            val slope = (targetHeight.toDouble() - startHeight.toDouble()) / terrainLine.size.toDouble() // m
            val terrainToTravel = terrainLine.drop(1)
            for ((index, terrain) in terrainToTravel.withIndex()) {
                // Calculate if the signal is above, at or through the current field
                // currentHeight (y) = mx + b
                val currentHeight = floor(slope * (index + 1) + startHeight.toDouble())
                currentPower = when {
                    // signal travels through the air (above ground)
                    currentHeight > terrain.height.toDouble() -> model.overAir(currentPower)
                    // signal travels through the ground
                    currentHeight < terrain.height.toDouble() -> model.throughGround(currentPower)
                    // signal travels along the terrain
                    else                                -> model.throughTerrain(currentPower, terrain.type)
                }
                if (currentPower <= model.minThreshold) {
                    break
                }
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