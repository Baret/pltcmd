package de.gleex.pltcmd.model.mapgenerators.data

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap

/**
 * A world that is currently being generated. When created it has a size but none of its tiles are filled yet.
 * It is a data container for coordinates and their corresponding terrain heights and maps but also provides
 * helpful methods for IntermediateGenerators.
 */
class MutableWorld(val bottomLeftCoordinate: Coordinate,
                   val worldSizeWidthInTiles: Int,
                   val worldSizeHeightInTiles: Int) {
    // public for now because of AbstractSquareMapGenerator
    val terrainMap = mutableMapOf<Coordinate, Pair<TerrainHeight?, TerrainType?>>()

    private val topRightCoordinate = bottomLeftCoordinate.
            withRelativeEasting(worldSizeWidthInTiles).
            withRelativeNorthing(worldSizeHeightInTiles)

    init {
        require(bottomLeftCoordinate.eastingFromLeft % Sector.TILE_COUNT == 0
                && bottomLeftCoordinate.northingFromBottom % Sector.TILE_COUNT == 0) {
            "Bottom left coordinate of a world must be a sector origin! Given: $bottomLeftCoordinate"
        }
        require(worldSizeWidthInTiles % Sector.TILE_COUNT == 0
                && worldSizeHeightInTiles % Sector.TILE_COUNT == 0) {
            "Only full sectors may fit in the world dimensions ($worldSizeWidthInTiles by $worldSizeHeightInTiles tiles is not valid)."
        }
    }

    fun toWorldMap(): WorldMap {
        // some require() checks to validate a full map has been generated -> i.e. no null terrain types and heights

        // generate sectors out of terrainMap
        val sectors = setOf<Sector>()
        return WorldMap(sectors)
    }

    /**
     * Puts both the [TerrainHeight] and the [TerrainType] of the given [Coordinate] to the values of the given [Terrain].
     */
    operator fun set(coordinate: Coordinate, terrain: Terrain) {
        terrainMap[coordinate] = Pair(terrain.height, terrain.type)
    }

    /**
     * Puts the [TerrainHeight] at the given [Coordinate] to the given value and keeps the [TerrainType].
     */
    operator fun set(coordinate: Coordinate, terrainHeight: TerrainHeight) {
        terrainMap[coordinate] = Pair(terrainHeight, terrainMap[coordinate]?.second)
    }

    /**
     * Puts the [TerrainType] at the given [Coordinate] to the given value and keeps the [TerrainHeight].
     */
    operator fun set(coordinate: Coordinate, terrainType: TerrainType) {
        terrainMap[coordinate] = Pair(terrainMap[coordinate]?.first, terrainType)
    }
}