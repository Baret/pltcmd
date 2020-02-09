package de.gleex.pltcmd.model.mapgenerators.data

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.*
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.stream.Collectors
import java.util.stream.StreamSupport

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
            withRelativeEasting(worldSizeWidthInTiles - 1).
            withRelativeNorthing(worldSizeHeightInTiles - 1)

    private val completeArea: CoordinateArea

    /**
     * The set of all [MainCoordinate]s contained in the area of this world.
     */
    val mainCoordinates: Set<MainCoordinate>
        get() {
            val coords = mutableSetOf<MainCoordinate>()
            // TODO: add step(100) to not ask every single coordinate when you only need every 100th
            for(c in bottomLeftCoordinate..topRightCoordinate) {
                coords.add(c.toMainCoordinate())
            }
            return coords
        }

    /**
     * The set of [MainCoordinate]s that contain tiles that have already been generated.
     */
    val mainCoordinatesNotEmpty: Set<MainCoordinate>
        get() {
            val notEmptyOnes = terrainMap.keys.map { it.toMainCoordinate() }.toSet()
            log.debug("Found ${notEmptyOnes.size} main coordinates with generated tiles")
            return notEmptyOnes
        }

    /**
     * All [MainCoordinate]s that are completely empty yet.
     */
    val mainCoordinatesEmpty: Set<MainCoordinate>
        get() {
            val emptyOnes = mainCoordinates - mainCoordinatesNotEmpty
            log.debug("Found ${emptyOnes.size} empty main coordinates")
            return emptyOnes
        }

    private val log = LoggerFactory.getLogger(this::class)

    init {
        require(bottomLeftCoordinate.eastingFromLeft % Sector.TILE_COUNT == 0
                && bottomLeftCoordinate.northingFromBottom % Sector.TILE_COUNT == 0) {
            "Bottom left coordinate of a world must be a sector origin! Given: $bottomLeftCoordinate"
        }
        require(worldSizeWidthInTiles % Sector.TILE_COUNT == 0
                && worldSizeHeightInTiles % Sector.TILE_COUNT == 0) {
            "Only full sectors may fit in the world dimensions ($worldSizeWidthInTiles by $worldSizeHeightInTiles tiles is not valid)."
        }
        completeArea = CoordinateArea(bottomLeftCoordinate..topRightCoordinate)
    }

    fun toWorldMap(): WorldMap {
        // some require() checks to validate a full map has been generated -> i.e. no null terrain types and heights
        log.debug("Creating world map from ${terrainMap.size} tiles")

        // generate sectors out of terrainMap
        val sectors = mutableSetOf<Sector>()
        for(sectorOriginNorthing in bottomLeftCoordinate.northingFromBottom..topRightCoordinate.northingFromBottom step Sector.TILE_COUNT) {
            for(sectorOriginEasting in bottomLeftCoordinate.eastingFromLeft..topRightCoordinate.eastingFromLeft step Sector.TILE_COUNT) {
                val tiles = mutableSetOf<WorldTile>()
                val sectorEndEasting = sectorOriginEasting + Sector.TILE_COUNT - 1
                val sectorEndNorthing = sectorOriginNorthing + Sector.TILE_COUNT - 1
                for(y in sectorOriginNorthing..sectorEndNorthing) {
                    for(x in sectorOriginEasting..sectorEndEasting) {
                        val currentCoordinate = Coordinate(x, y)
                        // expecting no null values here
                        val (height, type) = terrainMap[currentCoordinate]!!
                        tiles.add(WorldTile(currentCoordinate, Terrain.of(type!!, height!!)))
                    }
                }
                sectors.add(Sector(Coordinate(sectorOriginEasting, sectorOriginNorthing), tiles))
            }
        }
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

    /**
     * Returns the neighbors of the given coordinate that are in range of this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> =
            coordinate.neighbors().filter { it in bottomLeftCoordinate..topRightCoordinate }

    /**
     * Gets the height at the given coordinate, if present
     */
    fun heightAt(coordinate: Coordinate) = terrainMap[coordinate]?.first

    fun find(area: CoordinateArea = completeArea, predicate: (Coordinate) -> Boolean): Set<Coordinate> {
        log.debug("Finding in ${terrainMap.keys.size} coordinates...")
        return StreamSupport.
                stream(terrainMap.keys.spliterator(), true).
                filter { it in area }.
                filter(predicate).
                collect(Collectors.toSet())
    }

    operator fun contains(coordinate: Coordinate) = terrainMap.keys.contains(coordinate)
}