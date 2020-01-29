package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.world.WorldMap

/**
 * Generates a full WorldMap.
 */
interface MapGenerator {
    /**
     * Returns a newly generated world.
     */
    fun generateWorld(): WorldMap
}