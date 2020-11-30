package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.SignalPropagator
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.core.toSignalStrength
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Vision propagates really far through air but stops instantly when hitting the ground (obviously).
 *
 * @param maxRangeInTiles Used to limit the maximum view distance. Independent of the current
 * signal loss the vision stops at this range.
 */
class VisionPropagator(maxRangeInTiles: Double) : SignalPropagator {

    companion object {
        private val log = LoggerFactory.getLogger(VisionPropagator::class)
    }

    private var accumulatedSignalLoss: Double = 0.0

    private var remainingTiles = maxRangeInTiles

    private var hitGround = false

    override val remainingSignalStrength: SignalStrength
        get() = (1.0 - accumulatedSignalLoss).toSignalStrength()

    override val signalDepleted: Boolean
        get() = remainingTiles <= 0 || accumulatedSignalLoss >= 1.0

    override fun signalLossThroughAir() {
        if (log.isTraceEnabled()) {
            log.trace("\t\tSignal travels through air")
        }
        addSignalLoss(0.02)
    }

    override fun signalLossThroughGround() {
        if (log.isTraceEnabled()) {
            log.trace("\t\tSignal travels through ground. hitGround = $hitGround")
        }
        // You can poorly see the first tile uphill
        addSignalLoss(0.5)
        hitGround = true
    }

    override fun signalLossThroughTerrain(terrainType: TerrainType) {
        if (log.isTraceEnabled()) {
            log.trace("\t\tSignal travels through terrain $terrainType")
        }
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
        if (log.isTraceEnabled()) {
            log.trace("\t\t\tAdding $additionalSignalLoss. Currently accumulated: $accumulatedSignalLoss")
        }
        if (hitGround) {
            accumulatedSignalLoss += 1.0
            if (log.isTraceEnabled()) {
                log.trace("\t\t\tSignal has hit the ground! Adding extra signal loss! New value: $accumulatedSignalLoss")
            }
        }
        remainingTiles--
    }

}
