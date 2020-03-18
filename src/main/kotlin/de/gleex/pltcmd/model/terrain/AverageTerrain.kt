package de.gleex.pltcmd.model.terrain

import de.gleex.pltcmd.model.world.Coordinate
import java.util.*
import java.util.stream.Collectors

/** Holds all real world data that is mapped onto a single preview tile */
data class AverageTerrain(val mappedTiles: MutableMap<Coordinate, Pair<TerrainHeight?, TerrainType?>> = mutableMapOf()) {

    @Suppress("MemberVisibilityCanBePrivate")
    fun getAverageHeight(): TerrainHeight? {
        val average = mappedTiles.values.stream()
                .filter { it.first != null }
                .mapToInt { it.first!!.value }
                .average()
        if (!average.isPresent()) {
            return null
        }
        val averageHeightValue = average.asDouble.toInt()
        return TerrainHeight.ofValue(averageHeightValue)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getDominatingType(): TerrainType? {
        return mappedTiles.values.stream()
                .map { it.second }                  // get TerrainType
                .filter(Objects::nonNull)           // filter not yet generated
                .collect(Collectors.groupingBy<TerrainType?, TerrainType?> { it })  // collect same types
                .mapValues { it.value.size }        // count same types
                .maxBy { it.value }                 // take type that occurred most
                ?.key                               // which may be null
    }

    fun createTerrain(): Terrain? {
        val averageHeight = getAverageHeight()
        val dominatingType = getDominatingType()
        if (averageHeight == null && dominatingType == null) {
            return null
        }
        val type = dominatingType ?: TerrainType.WATER_DEEP
        val height = averageHeight ?: TerrainHeight.MIN
        return Terrain.of(type, height)
    }

}