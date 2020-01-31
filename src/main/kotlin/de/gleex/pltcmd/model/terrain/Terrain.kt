package de.gleex.pltcmd.model.terrain

class Terrain private constructor(val type: TerrainType, val height: TerrainHeight) {
    companion object {
        private val terrainObjects = mutableMapOf<Pair<TerrainType, TerrainHeight>, Terrain>()

        /**
         * Returns the terrain with the given type and height. As terrain is immutable, all objects are cached.
         * There is no need to have more than [number of terrain types] * [number of terrain heights] objects of this class.
         */
        fun of(type: TerrainType, height: TerrainHeight): Terrain {
            return terrainObjects.getOrPut(Pair(type, height)) { Terrain(type, height) }
        }

        /**
         * Return a randomly created terrain.
         */
        fun random() = of(TerrainType.values().random(), TerrainHeight.values().random())
    }

}