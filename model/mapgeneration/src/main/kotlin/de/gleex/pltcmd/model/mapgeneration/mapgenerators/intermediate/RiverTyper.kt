package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {}

class RiverTyper(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        // for now create one river on every mountain (expecting every mountain top to be a "target")
        val fullRivers = findRivers()
        fullRivers.forEach {
            val river = it.toList().dropLast(rand.nextInt(2, 6))
            log.debug { "Generating river of length ${river.size} from ${river.first()} to ${river.last()}" }
            river.forEach { coordinate ->
                mutableWorld[coordinate] = TerrainType.WATER_SHALLOW
            }
        }
    }

    /**
     * finds one river to every mountaintop (aka targets in context.mountainTops
     */
    private fun findRivers(): MutableList<List<Coordinate>> {
        val mountainTopsToReach = context.mountainTops.targets.toMutableSet().
            ifEmpty {
                log.debug { "No mountain tops found to create rivers." }
                return emptyList<List<Coordinate>>().toMutableList()
            }
        log.debug { "Creating rivers from ${mountainTopsToReach.size} mountain tops" }
        var tryingDistance = context.mountainTops.maxDistance
        val fullRivers = mutableListOf<List<Coordinate>>()
        log.debug { "Longest distance: $tryingDistance tiles. Starting to search river candidates..." }
        while (mountainTopsToReach.isNotEmpty() && tryingDistance >= 0) {
            context.mountainTops.allWithDistance(tryingDistance)
                .shuffled(rand)
                .forEach { riverEndCandidate ->
                    context
                        .mountainTops
                        .pathFrom(riverEndCandidate)
                        .takeIf { it.isNotEmpty() }
                        ?.let { riverCandidate ->
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
        log.debug { "Found ${fullRivers.size} full paths to create rivers." }
        return fullRivers
    }
}