package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {}

/**
 * Simply fills the given area with the given [TerrainType] at all locations that do not yet have a type.
 */
class FixedTypeFiller(
        private val terrainType: TerrainType,
        override val rand: Random,
        override val context: GenerationContext
) : IntermediateGenerator() {

    override fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld) {
        mutableWorld.find(area) {
            mutableWorld.typeAt(it) == null
        }.also {
            log.debug { "Filling up ${it.size} tiles with type $terrainType" }
        }.forEach {
            mutableWorld[it] = terrainType
        }
    }
}
