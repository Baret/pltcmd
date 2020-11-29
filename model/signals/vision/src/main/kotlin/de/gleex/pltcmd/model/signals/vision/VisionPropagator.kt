package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.SignalPropagator
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.core.toSignalStrength
import de.gleex.pltcmd.model.world.terrain.TerrainType

/**
 * Vision propagates really far through air but stops instantly when hitting the ground (obviously).
 *
 * @param maxRangeInTiles Used to limit the maximum view distance. Independent of the current
 * signal loss the vision stops at this range.
 */
class VisionPropagator(maxRangeInTiles: Double) : SignalPropagator {

    private var accumulatedSignalLoss: Double = 0.0

    private var remainingTiles = maxRangeInTiles

    override val remainingSignalStrength: SignalStrength
        get() = (1.0 - accumulatedSignalLoss).toSignalStrength()

    override val signalDepleted: Boolean
        get() = remainingTiles <= 0 || accumulatedSignalLoss >= 1.0

    override fun signalLossThroughAir() {
        addSignalLoss(0.02)
    }

    override fun signalLossThroughGround() {
        addSignalLoss(1.0)
    }

    override fun signalLossThroughTerrain(terrainType: TerrainType) {
        addSignalLoss(when (terrainType) {
            TerrainType.GRASSLAND     -> 0.05
            TerrainType.FOREST        -> 0.25
            TerrainType.HILL          -> 0.1
            TerrainType.MOUNTAIN      -> 0.4
            TerrainType.WATER_DEEP    -> 0.03
            TerrainType.WATER_SHALLOW -> 0.03
        })
    }

    private fun addSignalLoss(additionalSignalLoss: Double) {
        accumulatedSignalLoss += additionalSignalLoss
        remainingTiles--
    }

}
