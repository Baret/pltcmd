package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.world.Coordinate
import kotlin.random.Random

/**
 * Uses the same [Terrain] for the whole world.
 */
class PlainWorldGenerator(squareSideLengthInSectors: Int, val terrain: Terrain, override val rand: Random) : AbstractSquareMapGenerator(squareSideLengthInSectors, rand) {

    override fun createTerrain(tileCoordinate: Coordinate) = terrain

}