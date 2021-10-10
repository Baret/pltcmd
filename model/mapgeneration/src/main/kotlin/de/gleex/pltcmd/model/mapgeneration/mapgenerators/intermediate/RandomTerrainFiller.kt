package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.random.Random

/**
 * Fills an area with random [Terrain] and adds random [TerrainType] to coordinates that only contain a height.
 */
class RandomTerrainFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = KotlinLogging.logger {}

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        var emptyOnes = 0
        var missingTypeOnes = 0
        area.asSequence().forEach {
            if(it !in mutableWorld) {
                mutableWorld[it] = Terrain.random(rand)
                emptyOnes++
            } else if(mutableWorld.heightAt(it) != null && mutableWorld.typeAt(it) == null) {
                mutableWorld[it] = TerrainType.random(rand)
                missingTypeOnes++
            }
        }
        log.debug { "Filled up $missingTypeOnes coordinates with random terrain type, created $emptyOnes full tiles with random terrain" }
    }
}