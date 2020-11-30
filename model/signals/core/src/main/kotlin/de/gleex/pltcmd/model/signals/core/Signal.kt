package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.terrain.Terrain
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.floor

/**
 * A signal has an [origin] and an [area] that it can be received in. For every [Coordinate] in [area] the
 * [SignalStrength] can be calculated. The [SignalPower] determines it's range and thus the size of [area].
 *
 * @param origin the location from where this [Signal] is emitted
 * @param area the [WorldArea] that this [Signal] can be received in, preferably a circle. Every [Coordinate] outside of
 * this area has a [SignalStrength] of [SignalStrength.NONE]
 * @param power the power of this signal (in the end some arbitrary number of how "powerful" this signal is)
 */
class Signal<P: SignalPower>(
        val origin: Coordinate,
        val area: WorldArea,
        private val power: P
) {

    companion object {
        private val log = LoggerFactory.getLogger(Signal::class)
    }

    init {
        require(origin in area) {
            "The origin of a signal must be in its area! $origin is not in $area"
        }
    }

    /**
     * To only calculate every [SignalStrength] once, this map caches each result.
     */
    private val signalCache: MutableMap<Coordinate, SignalStrength> = mutableMapOf()

    /**
     * All [Coordinate]s of [area] mapped to their corresponding [SignalStrength]. The same as if
     * you would call [at] for every single coordinate.
     */
    val signalMap: Map<Coordinate, SignalStrength>
        // Calculate or load signals from cache for every coordinate in the area
        get() = area.associateWith { coordinate -> at(coordinate) }

    /**
     *
     */
    fun at(target: Coordinate): SignalStrength {
        return signalCache.computeIfAbsent(target) { calculateSignalStrengthAt(it) }
    }

    /**
     * Used to calculate the [SignalStrength] at the given coordinate. It creates a straight line
     * from [origin] to [target] and calls [along].
     */
    private fun calculateSignalStrengthAt(target: Coordinate): SignalStrength {
        if (log.isTraceEnabled()) {
            log.trace(" - - - calculating signal from $origin to $target")
        }
        val terrainList = area[CoordinatePath.line(origin, target)]
                .map { it.terrain }
        return along(terrainList).also {
            if (log.isTraceEnabled()) {
                log.trace(" - - - Finished calculating signal from $origin to $target. Result: $it")
            }
        }
    }

    /**
     * Calculates signal loss along the given terrain which is usually a straight line.
     *
     * @param terrainLine the line of terrain that the signal travels along **including** the origin
     * @return the [SignalStrength] at the end of the given terrain
     */
    private fun along(terrainLine: List<Terrain>): SignalStrength {
        val propagator: SignalPropagator = power.newSignalPropagator()
        if (terrainLine.size > 1) {
            val startHeight = terrainLine.first().height // b
            val targetHeight = terrainLine.last().height
            val slope = (targetHeight.toDouble() - startHeight.toDouble()) / terrainLine.size.toDouble() // m
            if (log.isTraceEnabled()) {
                log.trace("\tStarting terrain: ${terrainLine.first()}")
                log.trace("\tTarget terrain: ${terrainLine.last()}")
                log.trace("\tSlope: $slope")
            }
            val terrainToTravel = terrainLine.drop(1)
            for ((index, terrain) in terrainToTravel.withIndex()) {
                // Calculate if the signal is above, at or through the current field
                // currentHeight (y) = mx + b
                val currentHeight = floor(slope * (index + 1) + startHeight.toDouble())
                when {
                    // signal travels through the air (above ground)
                    currentHeight > terrain.height.toDouble() -> propagator.signalLossThroughAir()
                    // signal travels through the ground
                    currentHeight < terrain.height.toDouble() -> propagator.signalLossThroughGround()
                    // signal travels along the terrain
                    else                                      -> propagator.signalLossThroughTerrain(terrain.type)
                }
                if (propagator.signalDepleted) {
                    if (log.isTraceEnabled()) {
                        log.trace("\t XXX Short circuit! Signal depleted!")
                    }
                    break
                }
            }
        }
        return propagator.remainingSignalStrength
    }

}