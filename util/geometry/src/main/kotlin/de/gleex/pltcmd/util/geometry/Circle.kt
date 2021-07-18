package de.gleex.pltcmd.util.geometry

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Extend the radius to capture full tiles for a smoother appearance when centering the circle in the center of a tile
 * instead of the vertices (see https://www.redblobgames.com/grids/circle-drawing/#aesthetics)
 **/
val circleOffsetFromGrid = 0.5

/** Generates all points of the filled circle around the given center with the given [radius].  */
// implementation based on https://www.redblobgames.com/grids/circle-drawing/#bounding-circle
fun circleWithRadius(centerX: Int, centerY: Int, radius: Int, pointReceiver: (Int, Int) -> Unit) {
    val top = centerY - radius
    val bottom = centerY + radius

    val contentRadiusSquare = (radius + circleOffsetFromGrid).pow(2)
    for (y in top..bottom) {
        val dy = y - centerY
        val dx = floor(sqrt(contentRadiusSquare - dy * dy)).toInt()
        val left = centerX - dx
        val right = centerX + dx
        for (x in left..right) {
            pointReceiver.invoke(x, y)
        }
    }
}
