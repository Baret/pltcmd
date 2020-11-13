package de.gleex.pltcmd.model.signals.core

import de.gleex.pltcmd.model.world.terrain.TerrainType

interface PropagationModel {
    val minThreshold: Double // >= 0.0
    val maxRangeInTiles: Int
    fun overAir(value: Double): Double
    fun throughGround(value: Double): Double
    fun throughTerrain(value: Double, terrainType: TerrainType): Double
}