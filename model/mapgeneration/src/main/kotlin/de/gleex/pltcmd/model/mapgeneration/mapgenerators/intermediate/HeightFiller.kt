package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.extensions.normalDistributedInRange
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.extensions.higherOrEqual
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.extensions.lowerOrEqual
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import mu.KotlinLogging
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.random.Random

/**
 * Finds empty spaces and fills them with more or less smooth height.
 */
class HeightFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = KotlinLogging.logger {}

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        require(mutableWorld.mainCoordinatesNotEmpty.isNotEmpty()) {
            "Can not fill up a completely empty world!"
        }
        val edges = mutableWorld.find(area) {
            coordinate: Coordinate -> mutableWorld.neighborsOf(coordinate).filterNot { it in mutableWorld }.isNotEmpty()
        }
        log.debug("Found ${edges.size} edge tiles, filling up with terrain height...")
        workFrontier(edges, {currentCoordinate ->
            val unprocessedNeighbors =
                    mutableWorld.
                            neighborsOf(currentCoordinate).
                            filter { mutableWorld.heightAt(it) == null }

            unprocessedNeighbors.forEach { unprocessedNeighbor ->
                mutableWorld.
                        peekAhead(currentCoordinate, unprocessedNeighbor).
                        generateNext()
                unprocessedNeighbor.addToNextFrontier()
            }
        })
    }

    private fun MutableWorld.peekAhead(current: Coordinate, toGenerate: Coordinate): PeekResult {
        val peekRange = 21
        val direction = when {
            current.eastingFromLeft > toGenerate.eastingFromLeft       -> toGenerate..toGenerate.withRelativeEasting(-peekRange)
            current.eastingFromLeft < toGenerate.eastingFromLeft       -> toGenerate..toGenerate.withRelativeEasting(peekRange)
            current.northingFromBottom > toGenerate.northingFromBottom -> toGenerate..toGenerate.withRelativeNorthing(-peekRange)
            else                                                       -> toGenerate..toGenerate.withRelativeNorthing(peekRange)
        }
        val peekedTile = direction.flatMap { it.neighbors() }.filter { it != current }.firstOrNull { heightAt(it) != null }
        return PeekResult(current, toGenerate, peekedTile, this, rand, context.hilliness)
    }
}

private class PeekResult(
        private val current: Coordinate,
        private val toGenerate: Coordinate,
        private val peekedTile: Coordinate?,
        private val mutableWorld: MutableWorld,
        private val rand: Random,
        hilliness: Double
) {
    val averageHeight = (TerrainHeight.MIN.value + (TerrainHeight.MAX.value - TerrainHeight.MIN.value) * hilliness)

    fun generateNext() {
        val currentHeight = mutableWorld.heightAt(current)!!
        val heightDifferences = mutableWorld.neighborsOf(current).
                mapNotNull { mutableWorld.heightAt(it) }.
                map { currentHeight.value - it.value }
        // check if the difference to a neighbor is too big -> we MUST go up/down!
        when {
            heightDifferences.minOrNull()?:0 <= -3 -> {
                mutableWorld[toGenerate] = currentHeight + 1
            }
            heightDifferences.maxOrNull()?:0 >= 3  -> {
                mutableWorld[toGenerate] = currentHeight - 1
            }
            else                             -> {
                // else check how we approach a target height
                val targetHeight = if (peekedTile != null) {
                    mutableWorld.heightAt(peekedTile)!!
                } else {
                    // if nothing peeked, target a random height near hilliness
                    val randomHeightValue = rand.normalDistributedInRange(averageHeight, TerrainHeight.MIN.value, TerrainHeight.MAX.value)
                    TerrainHeight.ofValue(randomHeightValue)!!
                }
                val heightDiff = targetHeight.value - currentHeight.value
                val steepness = calcSteepness(heightDiff)
                mutableWorld[toGenerate] = when {
                    targetHeight > currentHeight -> currentHeight.higherOrEqual(rand, steepness)
                    targetHeight < currentHeight -> currentHeight.lowerOrEqual(rand, steepness)
                    else                         -> {
                        // same height has a chance to change
                        fullRandom(currentHeight)
                    }
                }
            }
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
        return min(((0.5 / 3) * heightDiff.absoluteValue.toDouble()) + 0.5, 1.0)
    }

}