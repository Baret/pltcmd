package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.terrain.TerrainType

/**
 * A signal propagator holds the state of the calculation of [SignalStrength]. When calculating
 * the [SignalStrength] at a location the signal propagates through the map. While propagating it
 * accumulates signal loss resulting in a [SignalStrength].
 *
 * While simulating the propagation of a signal a [SignalPropagator] is called multiple times before
 * asking it for the [remainingSignalStrength].
 */
interface SignalPropagator {
    /**
     * The [SignalStrength] that is currently left. It is ideally called only once after
     * signal loss accumulation is finished.
     */
    val remainingSignalStrength: SignalStrength

    /**
     * Returns true when so much signal loss has been accumulated that [remainingSignalStrength]
     * is [SignalStrength.NONE]. This may be used to short circuit the signal loss calculation.
     */
    val signalDepleted: Boolean
        get() = remainingSignalStrength <= SignalStrength.NONE

    /**
     * Called for each tile a signal travels through the air. This accumulates the least signal loss.
     */
    fun signalLossThroughAir()

    /**
     * Called for each tile a signal travels through ground. This usually accumulates the most signal loss.
     */
    fun signalLossThroughGround()

    /**
     * Called for each tile a signals travels through the given [TerrainType].
     */
    fun signalLossThroughTerrain(terrainType: TerrainType)
}
