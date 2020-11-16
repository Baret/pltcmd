package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.terrain.TerrainType

interface PropagationModel<P: SignalPower> {
    /**
     * A signal with this or less remaining [SignalStrength] is considered "no signal". This value must be
     * at least [SignalStrength.MIN_ALLOWED_VALUE] but may be higher for models reducing the strength by percent.
     */
    val minThreshold: Double // >= 0.0
    val maxRangeInTiles: Double
    fun overAir(value: Double): Double
    fun throughGround(value: Double): Double
    fun throughTerrain(value: Double, terrainType: TerrainType): Double
    fun toSignalStrength(calculationValue: Double): SignalStrength
}