package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Fills an area with random [Terrain] and adds random [TerrainType] to coordinates that only contain a height.
 */
class RandomTerrainFiller(override val rand: Random, override val context: GenerationContext) : IntermediateGenerator() {

    private val log = LoggerFactory.getLogger(RandomTerrainFiller::class)

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
        log.debug("Filled up $missingTypeOnes coordinates with random terrain type, created $emptyOnes full tiles with ranom terrain")
    }
}