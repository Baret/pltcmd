package de.gleex.pltcmd.model.terrain

import de.gleex.pltcmd.model.world.Coordinate
import java.util.*
import java.util.stream.Collectors

/** Holds all real world data that is mapped onto a single preview tile */
data class AverageTerrain(private val mappedTiles: MutableMap<Coordinate, TerrainData> = mutableMapOf()) {

    data class TerrainData(var height: TerrainHeight?, var type: TerrainType?)

    fun getAverageHeight(): TerrainHeight? {
        return mappedTiles.values
                .mapNotNull { it.height }
                .average()
    }

    fun getDominatingType(): TerrainType? {
        return mappedTiles.values.stream()
                .map { it.type }                    // get TerrainType
                .filter(Objects::nonNull)           // filter not yet generated
                .collect(Collectors.groupingBy<TerrainType?, TerrainType?> { it })  // collect same types
                .mapValues { it.value.size }        // count same types
                .maxBy { it.value }                 // take type that occurred most
                ?.key                               // which may be null
    }

    fun put(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val terrainData = mappedTiles.computeIfAbsent(coordinate) { TerrainData(null, null) }
        terrainData.height = terrainHeight
        terrainData.type = terrainType
    }

}