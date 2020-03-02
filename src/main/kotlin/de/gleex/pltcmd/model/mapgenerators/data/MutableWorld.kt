package de.gleex.pltcmd.model.mapgenerators.data

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.*
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * A world that is currently being generated. When created it has a size but none of its tiles are filled yet.
 * It is a data container for coordinates and their corresponding terrain heights and types but also provides
 * helpful methods for IntermediateGenerators.
 */
class MutableWorld(val bottomLeftCoordinate: Coordinate,
                   val worldSizeWidthInTiles: Int,
                   val worldSizeHeightInTiles: Int) {

    private val terrainMap = mutableMapOf<Coordinate, Pair<TerrainHeight?, TerrainType?>>()
    val topRightCoordinate = bottomLeftCoordinate.
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
        get() = terrainMap.keys.map { it.toMainCoordinate() }.toSet()

    /**
     * All [MainCoordinate]s that are completely empty yet.
     */
    val mainCoordinatesEmpty: Set<MainCoordinate>
        get() = mainCoordinates - mainCoordinatesNotEmpty

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

    /**
     * Creates a [WorldMap] from the Coordinates that have been set so far. This method fails if the current state of
     * this mutable world can not be used to generate a valid map.
     */
    fun toWorldMap(): WorldMap {
        log.debug("Creating world map from ${terrainMap.size} tiles")
        require(terrainMap.size == completeArea.size) {
            "${terrainMap.size} coordinates have been generated, but ${completeArea.size} are needed."
        }
        require(terrainMap.values.any { it.first == null || it.second == null }.not()) {
            "Not all coordinates contain generated terrain."
        }

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
     * Returns the neighbors of the given coordinate that are in range of this world if it is also inside this world.
     * Otherwise an empty list will be returned.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return if (coordinate isInBounds this) {
            coordinate.neighbors().filter { it isInBounds this }
        } else {
            emptyList()
        }
    }

    /**
     * Gets the height at the given coordinate, if present
     */
    fun heightAt(coordinate: Coordinate) = terrainMap[coordinate]?.first

    /**
     * Gets the terrain type at the given coordinate, if present
     */
    fun typeAt(coordinate: Coordinate) = terrainMap[coordinate]?.second

    /**
     * Returns all [Coordinate]s in the given area (default is the complete world) that match the given predicate (and are already present).
     */
    fun find(area: CoordinateArea = completeArea, predicate: (Coordinate) -> Boolean = {true}): Set<Coordinate> {
        return terrainMap.keys.
                filter { it in area }.
                filter(predicate).
                toSet()
    }

    /**
     * Checks if the given coordinate is inside the bounds of this mutable world
     */
    fun isInBounds(coordinate: Coordinate): Boolean {
        return bottomLeftCoordinate.eastingFromLeft <= coordinate.eastingFromLeft
                && bottomLeftCoordinate.northingFromBottom <= coordinate.northingFromBottom
                && topRightCoordinate.eastingFromLeft >= coordinate.eastingFromLeft
                && topRightCoordinate.northingFromBottom >= coordinate.northingFromBottom
    }

    private infix fun Coordinate.isInBounds(world: MutableWorld) = world.isInBounds(this)

    /**
     * Returns true if the given coordinate has already been (partly) generated.
     */
    operator fun contains(coordinate: Coordinate) = terrainMap.keys.contains(coordinate)
}