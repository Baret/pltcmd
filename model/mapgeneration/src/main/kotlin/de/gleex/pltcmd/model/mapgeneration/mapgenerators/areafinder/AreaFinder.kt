package de.gleex.pltcmd.model.mapgeneration.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea

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