package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.CoordinateRectangle
import kotlin.math.min

/**
 * First find rectangles of emptiness in the world. Than filters all rectangles that are smaller than the given size.
 * The large rectangles will be split to multiples of the maximum size if possible. The remainder may form rectangles of
 * up to minimum size.
 */
class SizedEmptyRectangleAreaFinder(val minWidth: Int, val minHeight: Int, val maxWidth: Int, val maxHeight: Int) : AreaFinder {
    private val emptyFinder = EmptyRectangleAreaFinder()

    override fun findAll(partialWorld: MutableWorld): Set<CoordinateRectangle> {
        val emptyRectangles = emptyFinder.findAll(partialWorld)
        return emptyRectangles.filter(this::hasMinimumSize)
                .flatMap(this::getAllRectangles)
                .toSet()
    }

    private fun hasMinimumSize(coordinateRectangle: CoordinateRectangle) =
            coordinateRectangle.hasMinimumSize(minWidth, minHeight)

    private fun getAllRectangles(rectangle: CoordinateRectangle): Set<CoordinateRectangle> {
        val splitted = mutableSetOf<CoordinateRectangle>()
        val start = rectangle.bottomLeftCoordinate
        // x and y are relative to given rectangles origin
        for (y in 0..rectangle.height step maxHeight) {
            val height = min(rectangle.height - y, maxHeight)
            val yEnd = y + height - 1 // -1 because the starting coordinate is also part of the rectangle
            for (x in 0..rectangle.width step maxWidth) {
                val width = min(rectangle.width - x, maxWidth)
                val xEnd = x + width - 1 // -1 because the starting coordinate is also part of the rectangle
                val part = CoordinateRectangle(start.withRelativeEasting(x).withRelativeNorthing(y), start.withRelativeEasting(xEnd).withRelativeNorthing(yEnd))
                // the last rectangle might be too small
                if (hasMinimumSize(part)) {
                    splitted.add(part)
                }
            }
        }
        assert(splitted.isNotEmpty()) { "at least one rectangle must fit" }
        return splitted
    }

}