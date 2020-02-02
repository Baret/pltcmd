package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.world.Coordinate

/**
 * Uses the same [Terrain] for the whole world.
 */
class PlainWorldGenerator(squareSideLengthInSectors: Int, val terrain: Terrain) : AbstractSquareMapGenerator(squareSideLengthInSectors) {

    override fun createTerrain(tileCoordinate: Coordinate) = terrain

}