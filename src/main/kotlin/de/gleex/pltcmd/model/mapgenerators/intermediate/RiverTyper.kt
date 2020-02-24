package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

class RiverTyper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {
    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        // for now create one river on every mountain (expecting every mountain top to be a "target")
        val fullRivers = findRivers()
        fullRivers.forEach {
            val river = it.toList().dropLast(rand.nextInt(2, 6))
            log.debug("Generating river of length ${river.size} from ${river.first()} to ${river.last()}")
            river.forEach { coordinate ->
                mutableWorld[coordinate] = TerrainType.WATER_SHALLOW
            }
        }
    }

    /**
     * finds one river to every mountaintop (aka targets in context.mountainTops
     */
    private fun findRivers(): MutableList<Sequence<Coordinate>> {
        val mountainTopsToReach = context.mountainTops.targets.toMutableSet().
            ifEmpty {
                log.debug("No mountain tops found to create rivers.")
                return emptyList<Sequence<Coordinate>>().toMutableList()
            }
        log.debug("Creating rivers from ${mountainTopsToReach.size} mountain tops")
        var tryingDistance = context.mountainTops.maxDistance
        val fullRivers = mutableListOf<Sequence<Coordinate>>()
        log.debug("Longest distance: $tryingDistance tiles. Starting to search river candidates...")
        while (mountainTopsToReach.isNotEmpty() && tryingDistance >= 0) {
            context.mountainTops.allWithDistance(tryingDistance)
                    .shuffled(rand)
                    .forEach { riverEndCandidate ->
                        context.mountainTops.pathFrom(riverEndCandidate).ifPresent { riverCandidate ->
                            // if the path leads to a mountain top that does not yet have a river to it
                            val currentMountainTop = riverCandidate.last()
                            if (mountainTopsToReach.contains(currentMountainTop)) {
                                fullRivers.add(riverCandidate)
                                mountainTopsToReach.remove(currentMountainTop)
                            }
                        }
                    }
            tryingDistance--
        }
        log.debug("Found ${fullRivers.size} full paths to create rivers.")
        return fullRivers
    }
}