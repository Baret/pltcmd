package de.gleex.pltcmd.util.geometry

import kotlin.math.abs


/**
 * Implementation from "A Rasterizing Algorithm for Drawing Curves" by Alois Zingl at http://members.chello.at/~easyfilter/Bresenham.pdf
 * See https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#All_cases
 */
fun pointsOfLine(x0: Int, y0: Int, x1: Int, y1: Int, pointReceiver: (Int, Int) -> Unit) {
    val deltaX = abs(x1 - x0)
    val signX = if (x0 < x1) 1 else -1
    val deltaY = -abs(y1 - y0)
    val signY = if (y0 < y1) 1 else -1

    var error = deltaX + deltaY /*  error of the diagonal step */
    var e2: Int
    var x = x0
    var y = y0
    while (x != x1 || y != y1) {
        pointReceiver.invoke(x, y)

        e2 = 2 * error
        if (e2 - deltaY >= 0) { /*  error of the diagonal step + error for x direction > 0 */
            error += deltaY
            x += signX
        }
        if (e2 - deltaX <= 0) { /*  error of the diagonal step + error for y direction < 0 */
            error += deltaX
            y += signY
        }
    }
    pointReceiver.invoke(x1, y1)
}
