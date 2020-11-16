package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.PropagationModel
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.terrain.TerrainType
import java.lang.Double.max
import java.lang.Double.min

class Vision(visualPower: VisionPower): PropagationModel<VisionPower> {
    override val minThreshold: Double = VisionPower.MIN_POWER
    override val maxRangeInTiles: Double = min(visualPower.power, SignalStrength.MAX_ALLOWED_VALUE / AIR_LOSS)

    companion object {
        private val AIR_LOSS = 0.02
    }

    override fun overAir(value: Double): Double {
        return value - AIR_LOSS
    }

    override fun throughGround(value: Double): Double {
        return VisionPower.MIN_POWER
    }

    override fun throughTerrain(value: Double, terrainType: TerrainType): Double {
        return value - 0.3
    }

    override fun toSignalStrength(calculationValue: Double): SignalStrength =
            SignalStrength(max(calculationValue, SignalStrength.MIN_ALLOWED_VALUE))

}
