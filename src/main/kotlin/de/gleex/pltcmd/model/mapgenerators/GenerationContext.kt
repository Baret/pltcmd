package de.gleex.pltcmd.model.mapgenerators

import kotlin.random.Random

/**
 * A context for generating an area of the map. Contains values from 0 to 1 (exclusive) for different attributes for the map.
 */
data class GenerationContext(
        val forest: Double,
        val mountain: Double,
        val water: Double,
        val urban: Double
) {
    companion object {
        fun fromRandom(random: Random) = GenerationContext(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
    }
}
