package de.gleex.pltcmd.model.terrain

import de.gleex.pltcmd.model.mapgenerators.data.TerrainData
import de.gleex.pltcmd.model.world.Coordinate

/** Collects multiple [TerrainData] and provides the average for all given data. */
class AverageTerrain {

    private val mappedTiles: MutableMap<Coordinate, TerrainData> = mutableMapOf()

    val averageHeight: TerrainHeight?
        get() = mappedTiles.values
                .mapNotNull { it.height }
                .average()

    val dominatingType: TerrainType?
        get() = mappedTiles.values
                .mapNotNull { it.type } // get TerrainType and filter not yet generated
                .groupingBy { it }      // collect same types
                .eachCount()            // count occurrences of each type
                .maxBy { it.value }     // take type that occurred most
                ?.key                   // which may be null

    /** Replaces the current terrain at the given coordinate with new values. */
    fun put(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        mappedTiles.computeIfAbsent(coordinate) { TerrainData() }
                .update(terrainHeight, terrainType)
    }

}
