package de.gleex.pltcmd.model.mapgenerators.extensions

import de.gleex.pltcmd.model.terrain.TerrainHeight
import kotlin.random.Random

/**
 * Returns a [TerrainHeight] that is either the same height or lower depending on [steepness].
 *
 * The higher [steepness] the higher the chance that a lower terrain will be returned (1.0 means always lower)
 */
fun TerrainHeight.lowerOrEqual(rand: Random, steepness: Double): TerrainHeight {
    return if(rand.nextDouble() < steepness) {
        this - 1
    } else {
        this
    }
}

/**
 * Returns a [TerrainHeight] that is either the same height or higher depending on [steepness].
 *
 * The higher [steepness] the higher the chance that a higher terrain will be returned (1.0 means always higher)
 */
fun TerrainHeight.higherOrEqual(rand: Random, steepness: Double): TerrainHeight {
    return if(rand.nextDouble() < steepness) {
        this + 1
    } else {
        this
    }
}