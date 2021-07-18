package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.geometry.circleOffsetFromGrid
import de.gleex.pltcmd.util.geometry.circleWithRadius
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/** A filled circle around a center. */
class CoordinateCircle(val center: Coordinate, val radius: Distance) : CoordinateArea({ center.fillCircle(radius) }) {

    override fun contains(coordinate: Coordinate): Boolean {
        return center.distanceTo(coordinate) <= radius + WorldTile.edgeLength * circleOffsetFromGrid
    }

}

fun Coordinate.fillCircle(radius: Distance): SortedSet<Coordinate> {
    val circle = ConcurrentSkipListSet<Coordinate>()
    val radiusInTiles = (radius.inUnit(DistanceUnit.meters) / WorldTile.edgeLength.inUnit(DistanceUnit.meters)).toInt()
    circleWithRadius(eastingFromLeft, northingFromBottom, radiusInTiles) { x, y ->
        circle.add(Coordinate(x, y))
    }
    return circle
}
