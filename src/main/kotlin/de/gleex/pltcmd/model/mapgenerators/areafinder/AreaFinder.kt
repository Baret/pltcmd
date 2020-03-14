package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.CoordinateArea

/**
 * Finds areas in a partly generated world. Implementations choose what defines an area, but normally it is a set of
 * connected tiles with a common criteria.
 */
interface AreaFinder {

    /**
     * Returns all found areas in the given world.
     */
    fun findAll(partialWorld: MutableWorld): Set<CoordinateArea>

}