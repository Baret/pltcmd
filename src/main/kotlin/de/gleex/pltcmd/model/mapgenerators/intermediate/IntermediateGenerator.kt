package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate

/**
 * An intermediate generator generates a part of the final world. It may be called before or after other
 * intermediate generators. And it might be called to generate only a part of the whole world.
 */
interface IntermediateGenerator {
    fun generateArea(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: MutableMap<Coordinate, Pair<TerrainHeight?, TerrainType?>>)
}
