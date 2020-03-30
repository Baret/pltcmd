package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.intermediate.*
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateRectangle
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * The WorldMapGenerator is the only class you need outside this package. It generates the world for the game given a seed.
 */
class WorldMapGenerator(private val seed: Long, val worldWidthInTiles: Int, val worldHeightInTiles: Int) {

    private val random = Random(seed)
    private val context = GenerationContext.fromRandom(random)

    private val log = LoggerFactory.getLogger(this::class)

    private val generators: List<IntermediateGenerator> = listOf(
            // different generators go here

            MountainTopHeightMapper(random, context),
            //RiverTyper(random, context),
            PlainsGenerator(random, context),
            HeightFiller(random, context),
            TypeFiller(random, context),
            RandomTerrainFiller(random, context)
    )

    private val listeners = mutableSetOf<MapGenerationListener>()

    val sizeInTiles = worldWidthInTiles * worldHeightInTiles

    fun addListener(listener: MapGenerationListener) {
        listeners += listener
    }

    fun removeListener(listener: MapGenerationListener) {
        listeners -= listener
    }

    fun generateWorld(bottomLeftCoordinate: Coordinate = Coordinate(0, 0)): WorldMap {
        val partiallyGeneratedWorld = MutableWorld(bottomLeftCoordinate, worldWidthInTiles, worldHeightInTiles)
        listeners.forEach(partiallyGeneratedWorld::addListener)
        try {
            log.info("Generating a random world with seed $seed of $worldWidthInTiles * $worldHeightInTiles = ${worldWidthInTiles * worldHeightInTiles} tiles between $bottomLeftCoordinate and ${partiallyGeneratedWorld.topRightCoordinate} with $context")

            val started = System.currentTimeMillis()

            val fullMapArea = CoordinateRectangle(bottomLeftCoordinate, partiallyGeneratedWorld.topRightCoordinate)
            generators.forEach {
                // There could also be a context for each MainCoordinate to have some kind of "biomes" (which could actually be introduced later).
                val intermediateStarted = System.currentTimeMillis()
                it.generateArea(
                        fullMapArea,
                        partiallyGeneratedWorld)
                log.debug("Generator ${it::class.simpleName} took ${System.currentTimeMillis() - intermediateStarted} ms")
            }
            val generationTime = System.currentTimeMillis() - started
            log.info("Map generation with seed $seed took $generationTime ms")
            return partiallyGeneratedWorld.toWorldMap()
        } finally {
            listeners.forEach(partiallyGeneratedWorld::removeListener)
        }
    }
}