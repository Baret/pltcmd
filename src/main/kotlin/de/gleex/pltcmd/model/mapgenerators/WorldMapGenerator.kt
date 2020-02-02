package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.mapgenerators.intermediate.IntermediateGenerator
import de.gleex.pltcmd.model.mapgenerators.intermediate.MountainTopGenerator
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import kotlin.random.Random

/**
 * The WorldMapGenerator is the only class you need outside this package. It generates the world for the game given a seed.
 */
class WorldMapGenerator(seed: Long) {
    private val random = Random(seed)
    private val context = GenerationContext.fromRandom(random)

    private val generators: List<IntermediateGenerator> = listOf(
            // different generators go here
            MountainTopGenerator()
    )

    fun generateWorld(widthInTiles: Int, heightInTiles: Int, bottomLeftCoordinate: Coordinate = Coordinate(0, 0)): WorldMap {
        // The map has to be mutable while it is being generated
        // And intermediate generators might only be interested in either type or height and if a tile has already been generated
        val terrainMap = mutableMapOf<Coordinate, Pair<TerrainHeight?, TerrainType?>>()
        val topRightCoordinate = bottomLeftCoordinate.
                withRelativeEasting(widthInTiles).
                withRelativeNorthing(heightInTiles)
        // Maybe use forEachIndexed and tell each generator which position it has? Or even the whole chain?
        generators.forEach {
            // this part might be more complicated later
            // for example each generator could only be called for certain areas of the map, depending on previous generation results.
            // There could also be a context for each MainCoordinate to have some kind of "biomes" (which could actually be introduced later).
            it.generateArea(
                    bottomLeftCoordinate,
                    topRightCoordinate,
                    terrainMap)
        }
        // some require() checks to validate a full map has been generated -> i.e. no null terrain types and heights

        // generate sectors out of terrainMap
        val sectors = setOf<Sector>()
        return WorldMap(sectors)
    }
}