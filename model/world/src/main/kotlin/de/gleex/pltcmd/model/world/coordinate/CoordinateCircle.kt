package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.geometry.circleOffsetFromGrid
import de.gleex.pltcmd.util.geometry.circleWithRadius
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.times
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/** A filled circle around a center. */
class CoordinateCircle(val center: Coordinate, val radius: Distance) : CoordinateArea({ center.fillCircle(radius) }) {

    override fun contains(coordinate: Coordinate): Boolean {
        return center.distanceTo(coordinate) <= radius + circleOffsetFromGrid * WorldTile.edgeLength
    }

}

fun Coordinate.fillCircle(radius: Distance): SortedSet<Coordinate> {
    val circle = ConcurrentSkipListSet<Coordinate>()
    val radiusInTiles = (radius / WorldTile.edgeLength).toInt()
    circleWithRadius(eastingFromLeft, northingFromBottom, radiusInTiles) { x, y ->
        circle.add(Coordinate(x, y))
    }
    return circle
}
