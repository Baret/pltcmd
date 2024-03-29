package de.gleex.pltcmd.model.mapgeneration.mapgenerators.data

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.*
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainData
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.Executors

private val log = KotlinLogging.logger {}

/**
 * A world that is currently being generated. When created it has a size but none of its tiles are filled yet.
 * It is a data container for coordinates and their corresponding terrain heights and types but also provides
 * helpful methods for IntermediateGenerators.
 */
class MutableWorld(
    val bottomLeftCoordinate: Coordinate = Coordinate(0, 0),
    val worldSizeWidthInTiles: Int = Sector.TILE_COUNT,
    val worldSizeHeightInTiles: Int = Sector.TILE_COUNT
) {

    private val terrainMap = mutableMapOf<Coordinate, TerrainData>()
    val topRightCoordinate = bottomLeftCoordinate.movedBy(worldSizeWidthInTiles - 1, worldSizeHeightInTiles - 1)

    private val completeArea: CoordinateRectangle
    private val listeners = mutableSetOf<MapGenerationListener>()
    private val eventExecutor = Executors.newSingleThreadExecutor() // single thread for sequential events

    /**
     * The set of all [MainCoordinate]s contained in the area of this world.
     */
    val mainCoordinates: SortedSet<MainCoordinate> by lazy {
        bottomLeftCoordinate.toMainCoordinate()..topRightCoordinate.toMainCoordinate()
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

    init {
        require(
            bottomLeftCoordinate.eastingFromLeft % Sector.TILE_COUNT == 0
                    && bottomLeftCoordinate.northingFromBottom % Sector.TILE_COUNT == 0
        ) {
            "Bottom left coordinate of a world must be a sector origin! Given: $bottomLeftCoordinate"
        }
        require(
            worldSizeWidthInTiles % Sector.TILE_COUNT == 0
                    && worldSizeHeightInTiles % Sector.TILE_COUNT == 0
        ) {
            "Only full sectors may fit in the world dimensions ($worldSizeWidthInTiles by $worldSizeHeightInTiles tiles is not valid)."
        }
        completeArea = CoordinateRectangle(bottomLeftCoordinate, topRightCoordinate)
    }

    /**
     * Creates a [WorldMap] from the Coordinates that have been set so far. This method fails if the current state of
     * this mutable world can not be used to generate a valid map.
     */
    fun toWorldMap(): WorldMap {
        finishGeneration()
        log.debug { "Creating world map from ${terrainMap.size} tiles" }
        require(terrainMap.size == completeArea.size) {
            "${terrainMap.size} coordinates have been generated, but ${completeArea.size} are needed."
        }
        require(terrainMap.values.all(TerrainData::isComplete)) {
            "Not all coordinates contain generated terrain."
        }

        return WorldMap.create(completeArea) { WorldTile(it, Terrain.of(terrainMap[it]!!)) }
    }

    private fun finishGeneration() {
        eventExecutor.shutdown()
    }

    /**
     * Puts both the [TerrainHeight] and the [TerrainType] of the given [Coordinate] to the values of the given [Terrain].
     */
    operator fun set(coordinate: Coordinate, terrain: Terrain) {
        set(coordinate, terrain.height, terrain.type)
    }

    /**
     * Puts the [TerrainHeight] at the given [Coordinate] to the given value and keeps the [TerrainType].
     */
    operator fun set(coordinate: Coordinate, terrainHeight: TerrainHeight) {
        set(coordinate, terrainHeight, terrainMap[coordinate]?.type)
    }

    /**
     * Puts the [TerrainType] at the given [Coordinate] to the given value and keeps the [TerrainHeight].
     */
    operator fun set(coordinate: Coordinate, terrainType: TerrainType) {
        set(coordinate, terrainMap[coordinate]?.height, terrainType)
    }

    private fun set(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        requireInBounds(coordinate)
        terrainMap.getOrPut(coordinate) { TerrainData() }
            .update(terrainHeight, terrainType)
        fireChange(coordinate, terrainHeight, terrainType)
    }

    private fun fireChange(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        // remember current listeners for async call
        val listenersToNotify = listeners.toSet()
        eventExecutor.execute {
            listenersToNotify.forEach {
                it.terrainGenerated(
                    coordinate,
                    terrainHeight,
                    terrainType
                )
            }
        }
    }

    fun addListener(listener: MapGenerationListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: MapGenerationListener) {
        listeners.remove(listener)
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
    fun heightAt(coordinate: Coordinate) = terrainMap[coordinate]?.height

    /**
     * Gets the terrain type at the given coordinate, if present
     */
    fun typeAt(coordinate: Coordinate) = terrainMap[coordinate]?.type

    /**
     * Returns all [Coordinate]s in the given area (default is the complete world) that match the given predicate (and are already present).
     * The result is sorted to easily traverse connected parts.
     */
    fun find(
        area: CoordinateArea = completeArea,
        predicate: CoordinateFilter = { true }
    ): SortedSet<Coordinate> {
        return terrainMap.keys.filter { it in area }.filter(predicate).toSortedSet()
    }

    /**
     * Returns all [Coordinate]s in the given area (default is the complete world) that match the given predicate that are not set in this world.
     * The result is sorted to easily traverse connected parts.
     */
    fun findEmpty(
        area: CoordinateArea = completeArea,
        predicate: CoordinateFilter = { true }
    ): Sequence<Coordinate> {
        val empty = area.asSequence() - terrainMap.keys
        return empty.filter(predicate)
    }

    private fun requireInBounds(coordinate: Coordinate) {
        require(isInBounds(coordinate)) { "Coordinate $coordinate is not inside this world $completeArea" }
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

    /**
     * Ensures that the resulting position is inside this world.
     * @return the given Coordinate or the nearest at the border of this map
     **/
    fun moveInside(location: Coordinate): Coordinate {
        return WorldMap.Utils.moveInside(location, bottomLeftCoordinate, topRightCoordinate)
    }

}