package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Generates a square world full of random [WorldTile]s.
 */
class RandomMapGenerator(squareSideLengthInSectors: Int) : AbstractSquareMapGenerator(squareSideLengthInSectors) {

    companion object {
        private val log = LoggerFactory.getLogger(RandomMapGenerator::class)
    }

    override fun generateWorld(): WorldMap {
        log.info("Generating a random world of ${squareSideLengthInSectors * squareSideLengthInSectors} sectors...")
        return super.generateWorld()
    }

    override fun createTerrain(tileCoordinate: Coordinate) = Terrain.of(randomType(), randomHeight())

    private fun randomType() = TerrainType.values().random()

    private fun randomHeight() = TerrainHeight.values().random()

}