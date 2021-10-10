package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate.*
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import mu.KotlinLogging
import kotlin.random.Random

/**
 * The WorldMapGenerator is the only class you need outside this package. It generates the world for the game given a seed.
 */
class WorldMapGenerator(private val seed: Long, val worldWidthInTiles: Int, val worldHeightInTiles: Int) {

    init {
        val minSize = Sector.TILE_COUNT * 2
        require(worldWidthInTiles >= minSize && worldHeightInTiles >= minSize) {
            "The world must contain at least one main coordinate. Minimum size required $minSize * $minSize tiles, got $worldWidthInTiles * $worldHeightInTiles"
        }
    }

    private val random = Random(seed)
    private val context = GenerationContext.Companion.fromRandom(random)

    private val log = KotlinLogging.logger {}

    private val generators: List<IntermediateGenerator> = listOf(
            // different generators go here

            MountainTopHeightMapper(random, context),
            RiverTyper(random, context),
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
        listeners.forEach { it.startGeneration(bottomLeftCoordinate) }
        listeners.forEach(partiallyGeneratedWorld::addListener)
        try {
            log.info("Generating a random world with seed $seed")
            log.info("\tContext: $context")
            log.info("\tWorld size: $worldWidthInTiles * $worldHeightInTiles = ${worldWidthInTiles * worldHeightInTiles} tiles")
            log.info("\tRanging from $bottomLeftCoordinate to ${partiallyGeneratedWorld.topRightCoordinate}")

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