package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Finds empty spaces and fills them with more or less smooth height.
 */
class HeightFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        val edges = terrainMap.find(area) {
            coordinate: Coordinate -> terrainMap.neighborsOf(coordinate).filterNot { it in terrainMap }.isNotEmpty()
        }
        log.debug("found ${edges.size} edge tiles.")
    }
}