package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate

class MountainTopGenerator(override val context: GenerationContext) : IntermediateGenerator {
    override fun generateArea(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: Map<Coordinate, Pair<TerrainHeight?, TerrainType?>>) {
        // pick random positions for mountain tops
        // set those to max height
        // from each position find the four neighbours that have no height yet
        // and go down one or none with a chance of let's say 50%
        // repeat
    }
}
