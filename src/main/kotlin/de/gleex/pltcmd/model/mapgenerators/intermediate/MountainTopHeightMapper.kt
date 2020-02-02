package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate

class MountainTopHeightMapper() : IntermediateGenerator {
    override fun generateArea(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: MutableWorld) {
        // pick random positions for mountain tops
        // set those to max height
        // from each position find the four neighbours that have no height yet
        // and go down one or none with a chance of let's say 50%
        // repeat
    }
}
