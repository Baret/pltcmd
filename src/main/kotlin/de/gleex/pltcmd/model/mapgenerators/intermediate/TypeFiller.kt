package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.terrain.TerrainType.*
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

class TypeFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        val allTiles = terrainMap.find(area) {
            terrainMap.typeAt(it) == null
        }

        log.debug("Found ${allTiles.size} tiles that need a terrain type")

        // manually create mountain tops
        allTiles.
            filter { terrainMap.heightAt(it) == TerrainHeight.MAX }.
            forEach {
                terrainMap[it] = MOUNTAIN
                processedTiles.add(it)
            }

        log.debug("Set ${processedTiles.size} mountain tops, working downwards from there...")

        workFrontier(processedTiles, {currentCoordinate ->
            terrainMap.neighborsOf(currentCoordinate).
                filter { it in area && terrainMap.typeAt(it) == null && it.isNotProcessed() }.
                forEach { neighbor ->
                    val probabilities = when(terrainMap.heightAt(neighbor)) {
                        TerrainHeight.NINE -> mapOf(
                                        0.7 to MOUNTAIN,
                                        0.9 to HILL,
                                        1.0 to FOREST)
                        TerrainHeight.EIGHT -> mapOf(
                                        0.4 to MOUNTAIN,
                                        0.8 to HILL,
                                        1.0 to FOREST)
                        TerrainHeight.SEVEN -> mapOf(
                                        0.5 to MOUNTAIN,
                                        0.75 to HILL,
                                        0.95 to FOREST,
                                        1.0 to GRASSLAND)

                        TerrainHeight.SIX, TerrainHeight.FIVE, TerrainHeight.FOUR, TerrainHeight.THREE
                                            -> if(terrainMap.typeAt(currentCoordinate) == GRASSLAND) {
                                            mapOf(
                                                0.7 to GRASSLAND,
                                                1.0 to FOREST
                                            )
                                        } else {
                                            mapOf(
                                                0.7 to FOREST,
                                                1.0 to GRASSLAND
                                            )
                                        }
                        TerrainHeight.TWO -> mapOf(
                                        0.8 to WATER_SHALLOW,
                                        1.0 to GRASSLAND)
                        TerrainHeight.ONE -> mapOf(
                                            1.0 to WATER_DEEP
                                        )
                        else              -> mapOf(1.0 to GRASSLAND)
                    }
                    generateWith(neighbor, terrainMap, probabilities)
                    neighbor.addToNextFrontier()
                }
        })
    }

    private fun generateWith(toGenerate: Coordinate, terrainMap: MutableWorld, typeProbabilities: Map<Double, TerrainType>) {
        val randomValue = rand.nextDouble()
        val type = typeProbabilities[typeProbabilities.keys.first { randomValue < it }] ?: typeProbabilities.values.random(rand)
        terrainMap[toGenerate] = type
    }
}
