package de.gleex.pltcmd.model.world.terrain

import kotlin.random.Random

enum class TerrainType {
    GRASSLAND,
    FOREST,
    HILL,
    MOUNTAIN,
    WATER_DEEP,
    WATER_SHALLOW;

    companion object {
        fun random(r: Random) = TerrainType.values().random(r)
    }
}
