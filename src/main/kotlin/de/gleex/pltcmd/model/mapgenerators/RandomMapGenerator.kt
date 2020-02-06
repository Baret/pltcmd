package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Generates a square world full of random [WorldTile]s.
 */
class RandomMapGenerator(squareSideLengthInSectors: Int, override val rand: Random) : AbstractSquareMapGenerator(squareSideLengthInSectors, rand) {

    companion object {
        private val log = LoggerFactory.getLogger(RandomMapGenerator::class)
    }

    override fun generateWorld(): WorldMap {
        log.info("Generating a random world of ${squareSideLengthInSectors * squareSideLengthInSectors} sectors...")
        val started = System.currentTimeMillis()
        val generatedWorld = super.generateWorld()
        val generationTime = System.currentTimeMillis() - started
        log.info("Map generation took $generationTime ms")
        return generatedWorld
    }

    override fun createTerrain(tileCoordinate: Coordinate) = Terrain.random(rand)
}