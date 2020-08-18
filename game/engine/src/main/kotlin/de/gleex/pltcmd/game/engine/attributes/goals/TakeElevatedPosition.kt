package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight

/**
 * Finds the highest elevation around the given position and pushes a [ReachDestination] to the destination if needed.
 */
class TakeElevatedPosition(currentPosition: Coordinate, worldMap: WorldMap) : Goal() {

    init {
        val currentHeight = worldMap[currentPosition].height
        val highestPosition: Pair<Coordinate, TerrainHeight> = worldMap
                .neighborsOf(currentPosition)
                .associateWith { worldMap[it].height }
                .maxBy { it.value }!!
                .toPair()

        if(highestPosition.second > currentHeight) {
            pushSubGoals(ReachDestination(highestPosition.first))
        }
    }
}