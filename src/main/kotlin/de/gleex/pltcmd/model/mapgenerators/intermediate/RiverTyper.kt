package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

class RiverTyper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator {
    companion object {
        private val log = LoggerFactory.getLogger(this::class)
    }

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        // for now create one river on every mountain (expecting every mountain top to be a "target")
        val fullRivers = findRivers()
        fullRivers.forEach {
            val river = it.toList().dropLast(rand.nextInt(2, 6))
            log.debug("Generating river of length ${river.size} from ${river.first()} to ${river.last()}")
            river.forEach { coordinate ->
                terrainMap[coordinate] = TerrainType.WATER_SHALLOW
            }
        }
    }

    /**
     * finds one river to every mountaintop (aka targets in context.mountainTops
     */
    private fun findRivers(): MutableList<Sequence<Coordinate>> {
        val mountainTopsToReach = context.mountainTops.targets.toMutableSet()
        var tryingDistance = context.mountainTops.maxDistance
        val fullRivers = mutableListOf<Sequence<Coordinate>>()
        while (mountainTopsToReach.isNotEmpty() && tryingDistance >= 0) {
            context.mountainTops.allWithDistance(tryingDistance)
                    .shuffled(rand)
                    .forEach { riverEndCandidate ->
                        val riverCandidate = context.mountainTops.pathFrom(riverEndCandidate)
                        // if we the path leads to a mountain top that does not yet have a river to it
                        val currentMountainTop = riverCandidate.get().last()
                        if (riverCandidate.isPresent && mountainTopsToReach.contains(currentMountainTop)) {
                            fullRivers.add(riverCandidate.get())
                            mountainTopsToReach.remove(currentMountainTop)
                        }
                    }
            tryingDistance--
        }
        return fullRivers
    }
}