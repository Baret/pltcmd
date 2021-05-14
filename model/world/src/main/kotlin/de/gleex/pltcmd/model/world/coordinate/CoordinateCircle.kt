package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.util.geometry.circleWithRadius
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/** A filled circle around a center. */
class CoordinateCircle(val center: Coordinate, val radius: Int) : CoordinateArea({ center.fillCircle(radius) }) {

    override fun contains(coordinate: Coordinate): Boolean {
        return center.distanceTo(coordinate) <= radius.coordinates
    }

}

fun Coordinate.fillCircle(radius: Int): SortedSet<Coordinate> {
    val circle = ConcurrentSkipListSet<Coordinate>()
    circleWithRadius(eastingFromLeft, northingFromBottom, radius) { x, y ->
        circle.add(Coordinate(x, y))
    }
    return circle
}
