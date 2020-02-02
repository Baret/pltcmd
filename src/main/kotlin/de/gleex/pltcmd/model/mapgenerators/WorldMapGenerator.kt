package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.intermediate.IntermediateGenerator
import de.gleex.pltcmd.model.mapgenerators.intermediate.MountainTopHeightMapper
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * The WorldMapGenerator is the only class you need outside this package. It generates the world for the game given a seed.
 */
class WorldMapGenerator(
        seed: Long,
        private val worldSizeWidthInTiles: Int = GameOptions.SECTORS_COUNT_H * Sector.TILE_COUNT,
        private val worldSizeHeightInTiles: Int = GameOptions.SECTORS_COUNT_V * Sector.TILE_COUNT
) {
    private val random = Random(seed)
    private val context = GenerationContext.fromRandom(random)

    private val generators: List<IntermediateGenerator> = listOf(
            // different generators go here
            // GenericHeightMapper()
            RandomMapGenerator(worldSizeWidthInTiles / Sector.TILE_COUNT),
            MountainTopHeightMapper()

            // examples...
            // terrain type generator(s)
            // "filler" or "smoother" that makes sure there are no holes in the map
            // Advanced generators that rely on specific terrain features to put different heights/types over it, i.e. cities, lakes, rivers...
    )

    fun generateWorld(bottomLeftCoordinate: Coordinate = Coordinate(0, 0)): WorldMap {
        // The map has to be mutable while it is being generated
        // And intermediate generators might only be interested in either type or height and if a tile has already been generated
        val partiallyGeneratedWorld = MutableWorld(bottomLeftCoordinate, worldSizeWidthInTiles, worldSizeHeightInTiles)
        val topRightCoordinate = bottomLeftCoordinate.
                withRelativeEasting(worldSizeWidthInTiles).
                withRelativeNorthing(worldSizeHeightInTiles)
        log.info("Generating a random world of ${worldSizeWidthInTiles * worldSizeHeightInTiles} tiles...")
        val started = System.currentTimeMillis()
        // Maybe use forEachIndexed and tell each generator which position it has? Or even the whole chain?
        generators.forEach {
            // this part might be more complicated later
            // for example each generator could only be called for certain areas of the map, depending on previous generation results.
            // There could also be a context for each MainCoordinate to have some kind of "biomes" (which could actually be introduced later).
            it.generateArea(
                    bottomLeftCoordinate,
                    topRightCoordinate,
                    partiallyGeneratedWorld)
        }
        val generationTime = System.currentTimeMillis() - started
        log.info("Map generation took $generationTime ms")
        return partiallyGeneratedWorld.toWorldMap()
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class)
    }
}