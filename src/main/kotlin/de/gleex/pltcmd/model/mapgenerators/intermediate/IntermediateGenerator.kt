package de.gleex.pltcmd.model.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * An intermediate generator generates a part of the final world. It may be called before or after other
 * intermediate generators. And it might be called to generate only a part of the whole world.
 */
interface IntermediateGenerator {
    val rand: Random
    val context: GenerationContext

    fun generateArea(area: CoordinateArea, terrainMap: MutableWorld)
}
