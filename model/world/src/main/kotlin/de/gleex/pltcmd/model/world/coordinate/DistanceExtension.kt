package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.util.measure.distance.Distance

/**
 * The [Distance] of this many [Coordinate]s.
 *
 * @see Coordinate.edgeLength
 */
val Int.coordinates: Distance
    get() = Coordinate.edgeLength * this