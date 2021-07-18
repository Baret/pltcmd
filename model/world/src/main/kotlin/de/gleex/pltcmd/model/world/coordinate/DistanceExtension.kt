package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.distance.Distance

/**
 * The [Distance] of this many [WorldTile]s.
 *
 * @see WorldTile.edgeLength
 */
val Int.tilesAway: Distance
    get() = WorldTile.edgeLength * this