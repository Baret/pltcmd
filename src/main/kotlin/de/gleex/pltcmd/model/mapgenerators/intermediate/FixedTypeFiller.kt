package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * Simply fills the given area with the given [TerrainType] at all locations that do not yet have a type.
 */
class FixedTypeFiller(private val terrainType: TerrainType) : IntermediateGenerator() {

    override val rand: Random
        get() = Random

    override val context: GenerationContext
        get() = GenerationContext.fromRandom(rand)

    private val log = LoggerFactory.getLogger(this::class)

    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        terrainMap.find(area) {
            terrainMap.typeAt(it) == null
        }.also {
            log.debug("Filling up ${it.size} tiles with type $terrainType")
        }.forEach {
            terrainMap[it] = terrainType
        }
    }
}
