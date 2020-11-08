package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.util.geometry.circleWithRadius
import java.util.*

/** A filled circle around a center. */
class CoordinateCircle(center: Coordinate, radius: Int) : CoordinateArea(center.fillCircle(radius))

fun Coordinate.fillCircle(radius: Int): SortedSet<Coordinate> {
    val circle = TreeSet<Coordinate>()
    circleWithRadius(eastingFromLeft, northingFromBottom, radius) { x, y ->
        circle.add(Coordinate(x, y))
    }
    return circle
}
