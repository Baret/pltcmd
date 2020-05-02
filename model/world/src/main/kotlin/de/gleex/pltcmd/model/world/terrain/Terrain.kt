package de.gleex.pltcmd.model.world.terrain

import kotlin.random.Random

data class Terrain private constructor(val type: TerrainType, val height: TerrainHeight) {

    companion object {
        private val terrainObjects = mutableMapOf<TerrainData, Terrain>()

        /**
         * see of(TerrainType, TerrainHeight)
         * @throws IllegalArgumentException if !data.isComplete()
         **/
        fun of(data: TerrainData): Terrain {
            return terrainObjects.getOrPut(data) {
                require(data.isComplete()) { "Only complete TerrainData can be used to create Terrain! Got $data" }
                return Terrain(data.type!!, data.height!!)
            }
        }

        /**
         * Returns the terrain with the given type and height. As terrain is immutable, all objects are cached.
         * There is no need to have more than [number of terrain types] * [number of terrain heights] objects of this class.
         */
        fun of(type: TerrainType, height: TerrainHeight): Terrain {
            return of(TerrainData(height, type))
        }

        /**
         * Return a randomly created terrain.
         */
        fun random(r: Random) = of(TerrainType.values()
                .random(r), TerrainHeight.random(r))
    }

}