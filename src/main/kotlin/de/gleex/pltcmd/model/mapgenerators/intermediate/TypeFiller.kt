package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.terrain.TerrainType.*
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

class TypeFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        val allTiles = mutableWorld.find(area) {
            mutableWorld.typeAt(it) == null
        }

        log.debug("Found ${allTiles.size} tiles that need a terrain type")

        // manually create mountain tops
        allTiles.
            filter { mutableWorld.heightAt(it) == TerrainHeight.MAX }.
            forEach {
                mutableWorld[it] = MOUNTAIN
                processedTiles.add(it)
            }

        log.debug("Set ${processedTiles.size} mountain tops, working downwards from there...")

        workFrontier(processedTiles, {currentCoordinate ->
            mutableWorld.neighborsOf(currentCoordinate).
                filter { it in area && mutableWorld.typeAt(it) == null && it.isNotProcessed() }.
                forEach { neighbor ->
                    val probabilities = probabilities(mutableWorld.heightAt(neighbor), mutableWorld.typeAt(currentCoordinate))
                    mutableWorld[neighbor] = generateType(probabilities)
                    neighbor.addToNextFrontier()
                }
        })
    }

    private fun probabilities(terrainHeight: TerrainHeight?, neighborType: TerrainType?): Map<Double, TerrainType> {
        val richVegetation = context.vegetation
        val poorVegetation = 1.0 - richVegetation
        return when (terrainHeight) {
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
