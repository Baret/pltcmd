package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.Coordinate.Companion.compareByDistanceFrom
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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
class Signal<P : SignalPower>(
    val origin: Coordinate,
    val area: WorldArea,
    val power: P
) {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    init {
        require(origin in area) {
            "The origin of a signal must be in its area! $origin is not in $area"
        }
    }

    /**
     * To only calculate every [SignalStrength] once, this map caches each result.
     */
    private val signalCache: MutableMap<Coordinate, SignalStrength> = ConcurrentHashMap()

    private val coordinatesByDistanceToOrigin: SortedSet<Coordinate> = area
        .toSortedSet(compareByDistanceFrom(origin))

    /**
     * A flow of calls to [at] for every coordinate in [area]. It fills the area from inner to outer
     * (going outwards from [origin]).
     *
     * This flow is cancellable.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val all: Flow<Pair<Coordinate, SignalStrength>> =
        coordinatesByDistanceToOrigin
            .asFlow()
            .map { it to at(it) }
            .onStart { log.debug { "Starting to fill cache (current size ${signalCache.size})..." } }
            .onCompletion { log.debug { "...finished filling cache (current size ${signalCache.size})!" } }
            .buffer(Channel.UNLIMITED)
            .cancellable()

    /**
     * @return the [SignalStrength] of this signal at the given target. All [Coordinate]s outside
     * of [area] are automatically considered [SignalStrength.NONE].
     */
    fun at(target: Coordinate): SignalStrength {
        log.trace { "Calculating signal strength at $target" }
        return if (target in area) {
            signalCache.computeIfAbsent(target) { calculateSignalStrengthAt(it) }
        } else {
            SignalStrength.NONE
        }
    }

    /**
     * Used to calculate the [SignalStrength] at the given coordinate. It creates a straight line
     * from [origin] to [target] and calls [along].
     */
    private fun calculateSignalStrengthAt(target: Coordinate): SignalStrength {
        var start: Long? = null
        if (log.isTraceEnabled) {
            start = System.currentTimeMillis()
            log.trace { " - - - calculating signal from $origin to $target" }
        }
        val terrainList = area[CoordinatePath.line(origin, target)]
            .map { it.terrain }
        return along(terrainList).also {
            if (log.isTraceEnabled) {
                val duration = System.currentTimeMillis() - start!!
                log.trace { " - - - Finished calculating signal from $origin to $target in $duration ms. Result: $it" }
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
            log.trace { "\tStarting terrain: ${terrainLine.first()}" }
            log.trace { "\tTarget terrain: ${terrainLine.last()}" }
            log.trace { "\tSlope: $slope" }
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
                    log.trace { "\t XXX Short circuit! Signal depleted!" }
                    break
                }
            }
        }
        return propagator.remainingSignalStrength
    }
}