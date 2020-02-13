package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.extensions.higherOrEqual
import de.gleex.pltcmd.model.mapgenerators.extensions.lowerOrEqual
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.min
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
            val unprocessedNeighbors =
                    terrainMap.
                            neighborsOf(currentCoordinate).
                            filter { terrainMap.heightAt(it) == null }

            unprocessedNeighbors.forEach { unprocessedNeighbor ->
                terrainMap.
                        peekAhead(currentCoordinate, unprocessedNeighbor).
                        generateNext()
                unprocessedNeighbor.addToNextFrontier()
            }
        })
    }

    private fun MutableWorld.peekAhead(current: Coordinate, toGenerate: Coordinate): PeekResult {
        val peekRange = 21
        val direction = when {
            current.eastingFromLeft > toGenerate.eastingFromLeft       -> current..current.withRelativeEasting(-peekRange)
            current.eastingFromLeft < toGenerate.eastingFromLeft       -> current..current.withRelativeEasting(peekRange)
            current.northingFromBottom > toGenerate.northingFromBottom -> current..current.withRelativeNorthing(-peekRange)
            else                                                       -> current..current.withRelativeNorthing(peekRange)
        }
        val peekedTile = direction.firstOrNull { heightAt(it) != null }
        return PeekResult(current, toGenerate, peekedTile, this, rand)
    }
}

private class PeekResult(
        private val current: Coordinate,
        private val toGenerate: Coordinate,
        private val peekedTile: Coordinate?,
        private val mutableWorld: MutableWorld,
        val rand: Random
) {
    fun generateNext() {
        val currentHeight = mutableWorld.heightAt(current)!!
        val heightDifferences = mutableWorld.neighborsOf(current).
                mapNotNull { mutableWorld.heightAt(it) }.
                map { currentHeight.value - it.value }
        // check if the difference to a neighbor is too big -> we MUST go up/down!
        if(heightDifferences.min()?:0 <= -3) {
            mutableWorld[toGenerate] = currentHeight + 1
        } else if(heightDifferences.max()?:0 >= 3) {
            mutableWorld[toGenerate] = currentHeight - 1
        } else {
            // else check if we approach a target height
            if(peekedTile != null) {
                val targetHeight = mutableWorld.heightAt(peekedTile)!!
                val heightDiff = targetHeight.value - currentHeight.value
                val steepness = calcSteepness(heightDiff)
                if(targetHeight > currentHeight) {
                    mutableWorld[toGenerate] = currentHeight.higherOrEqual(rand, steepness)
                } else if(targetHeight < currentHeight) {
                    mutableWorld[toGenerate] = currentHeight.lowerOrEqual(rand, steepness)
                }
            }
        }

        // nothing happened? Let's go full random
        if(mutableWorld.heightAt(toGenerate) == null) {
            mutableWorld[toGenerate] = fullRandom(currentHeight)
        }
    }

    private fun fullRandom(currentHeight: TerrainHeight): TerrainHeight {
        val options = mutableListOf(currentHeight)
        if(currentHeight < TerrainHeight.MAX) {
            options += currentHeight + 1
        }
        if(currentHeight > TerrainHeight.MIN) {
            options += currentHeight - 1
        }
        return options.random(rand)
    }

    /**
     * calculates the steepness (probability to go up/down) when approaching a target height.
     *
     * A difference >= 3 means we MUST go up/down, which means steepness must be 1.0
     */
    private fun calcSteepness(heightDiff: Int): Double {
        return min(((0.5 / 3) * heightDiff.toDouble()) + 0.5, 1.0)
    }

}