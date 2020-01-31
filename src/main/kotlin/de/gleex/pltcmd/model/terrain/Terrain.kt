package de.gleex.pltcmd.model.terrain

data class Terrain private constructor(val type: TerrainType, val height: TerrainHeight) {
    companion object {
        private val terrainObjects = mutableMapOf<Pair<TerrainType, TerrainHeight>, Terrain>()

        fun of(type: TerrainType, height: TerrainHeight): Terrain {
            return terrainObjects.getOrPut(Pair(type, height)) { Terrain(type, height) }
        }
    }

}