package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.PropagationModel
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlin.math.roundToInt

class Vision(visualPower: VisionPower): PropagationModel {
    override val minThreshold: Double = 0.1
    override val maxRangeInTiles: Int = visualPower.power.roundToInt()

    override fun overAir(value: Double): Double {
        return value - 0.03
    }

    override fun throughGround(value: Double): Double {
        return 0.0
    }

    override fun throughTerrain(value: Double, terrainType: TerrainType): Double {
        return value - 0.3
    }

}
