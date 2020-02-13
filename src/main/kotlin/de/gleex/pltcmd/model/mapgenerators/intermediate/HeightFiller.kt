package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.extensions.higherOrEqual
import de.gleex.pltcmd.model.mapgenerators.extensions.lowerOrEqual
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Finds empty spaces and fills them with more or less smooth height.
 */
class HeightFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        require(terrainMap.mainCoordinatesNotEmpty.isNotEmpty()) {
            "Can not fill up a completely empty world!"
        }
        val edges = terrainMap.find(area) {
            coordinate: Coordinate -> terrainMap.neighborsOf(coordinate).filterNot { it in terrainMap }.isNotEmpty()
        }
        log.debug("found ${edges.size} edge tiles, filling up with terrain height...")
        workFrontier(edges, {currentCoordinate ->
            val (unprocessedNeighbors, processedNeighbors) =
                    terrainMap.
                            neighborsOf(currentCoordinate).
                            partition { terrainMap.heightAt(it) == null }
            val currentHeight = terrainMap.heightAt(currentCoordinate)!!
            val avgHeight = processedNeighbors.
                    mapNotNull { terrainMap.heightAt(it) }.
                    map { it.value.toDouble() }.
                    average()

            unprocessedNeighbors.forEach { unprocessed ->
                if(currentHeight.value.toDouble() >= avgHeight) {
                    terrainMap[unprocessed] = currentHeight.lowerOrEqual(rand, 0.5)
                } else {
                    terrainMap[unprocessed] = currentHeight.higherOrEqual(rand, 0.5)
                }
                unprocessed.addToNextFrontier()
            }
        })
    }
}