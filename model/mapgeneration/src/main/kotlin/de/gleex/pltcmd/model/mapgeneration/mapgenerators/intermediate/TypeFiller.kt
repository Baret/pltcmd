package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.terrain.TerrainType.*
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*
import kotlin.random.Random

class TypeFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        val allTiles = mutableWorld.find(area) {
            mutableWorld.typeAt(it) == null
        }

        log.debug("Found ${allTiles.size} tiles that need a terrain type")

        // start with highest points in the world
        val highestPoints = findHighestPoints(allTiles, mutableWorld)
        highestPoints.forEach {
            fillTypeBasedOnHeight(it, MOUNTAIN, mutableWorld)
            processedTiles.add(it)
        }
        log.debug("Started with ${processedTiles.size} highest points, working downwards from there...")

        workFrontier(processedTiles, {currentCoordinate ->
            mutableWorld.neighborsOf(currentCoordinate).
                filter { it in area && mutableWorld.typeAt(it) == null && it.isNotProcessed() }.
                forEach { neighbor ->
                    fillTypeBasedOnHeight(neighbor, mutableWorld.typeAt(currentCoordinate), mutableWorld)
                    neighbor.addToNextFrontier()
                }
        })
    }

    private fun findHighestPoints(allTiles: SortedSet<Coordinate>, mutableWorld: MutableWorld): MutableSet<Coordinate> {
        val mountainTops = mutableSetOf<Coordinate>()
        var maxHeight = TerrainHeight.MIN
        for (tile in allTiles) {
            val heightAtTile = mutableWorld.heightAt(tile) ?: continue
            if (heightAtTile > maxHeight) {
                maxHeight = heightAtTile
                mountainTops.clear()
            }
            if (heightAtTile == maxHeight) {
                mountainTops.add(tile)
            }
        }
        return mountainTops
    }

    private fun fillTypeBasedOnHeight(tileToFill: Coordinate, neighborType: TerrainType?, mutableWorld: MutableWorld) {
        val probabilities = probabilities(mutableWorld.heightAt(tileToFill), neighborType)
        mutableWorld[tileToFill] = generateType(probabilities)
    }

    private fun probabilities(terrainHeight: TerrainHeight?, neighborType: TerrainType?): Map<Double, TerrainType> {
        val richVegetation = context.vegetation
        val poorVegetation = 1.0 - richVegetation
        return when (terrainHeight) {
            TerrainHeight.TEN   -> mapOf(1.0 to MOUNTAIN)
            TerrainHeight.NINE  -> mapOf(
                    0.7 to MOUNTAIN,
                    0.9 to HILL,
                    1.0 to FOREST)
            TerrainHeight.EIGHT -> mapOf(
                    0.4 to MOUNTAIN,
                    (0.8 + 0.2 * poorVegetation) to HILL,
                    1.0 to FOREST)
            TerrainHeight.SEVEN -> mapOf(
                    0.5 to MOUNTAIN,
                    0.75 to HILL,
                    (0.75 + 0.25 * richVegetation) to FOREST,
                    1.0 to GRASSLAND)
            TerrainHeight.SIX, TerrainHeight.FIVE, TerrainHeight.FOUR, TerrainHeight.THREE
            -> if (neighborType == GRASSLAND) {
                mapOf(
                        0.7 * poorVegetation to GRASSLAND,
                        1.0 to FOREST
                )
            } else {
                mapOf(
                        0.7 * richVegetation to FOREST,
                        1.0 to GRASSLAND
                )
            }
            TerrainHeight.TWO   -> mapOf(
                    0.8 to WATER_SHALLOW,
                    1.0 to GRASSLAND)
            TerrainHeight.ONE   -> mapOf(
                    1.0 to WATER_DEEP
            )
            else                -> mapOf(1.0 to GRASSLAND)
        }
    }

    private fun generateType(typeProbabilities: Map<Double, TerrainType>): TerrainType {
        val randomValue = rand.nextDouble()
        return typeProbabilities.entries.first { randomValue <= it.key }.value
    }
}
