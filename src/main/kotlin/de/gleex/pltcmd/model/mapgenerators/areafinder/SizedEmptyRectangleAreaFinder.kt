package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.CoordinateRectangle
import kotlin.math.min

/**
 * First find rectangles of emptiness in the world. Than filters all rectangles that are smaller than the given size.
 * The large rectangles will be split to multiples of the maximum size if possible. The remainder may form rectangles of
 * at least minimum size.
 */
class SizedEmptyRectangleAreaFinder(val minWidth: Int, val minHeight: Int, val maxWidth: Int, val maxHeight: Int) : AreaFinder {
    private val emptyFinder = EmptyRectangleAreaFinder()

    override fun findAll(partialWorld: MutableWorld): Set<CoordinateRectangle> {
        val emptyRectangles = emptyFinder.findAll(partialWorld)
        return emptyRectangles.filter { it.hasMinimumSize() }
                .flatMap(this::getAllRectangles)
                .toSet()
    }

    private fun CoordinateRectangle.hasMinimumSize() = hasMinimumSize(minWidth, minHeight)

    /**
     * Splits the given rectangle in multiple rectangles if it exceeds the maximum size.
     * @param rectangle a rectangle with at least minimum size
     */
    private fun getAllRectangles(rectangle: CoordinateRectangle): Set<CoordinateRectangle> {
        val rectanglesWithWantedSize = mutableSetOf<CoordinateRectangle>()
        val start = rectangle.bottomLeftCoordinate
        val fullRectHeight = rectangle.height
        val fullRectWidth = rectangle.width
        // x and y are relative to the given rectangles origin!
        for (y in 0..fullRectHeight step maxHeight) {
            val height = min(fullRectHeight - y, maxHeight)
            for (x in 0..fullRectWidth step maxWidth) {
                val width = min(fullRectWidth - x, maxWidth)

                val wantedSizeRectangle = CoordinateRectangle(start.movedBy(x, y), width, height)
                // the last rectangle might be too small
                if (wantedSizeRectangle.hasMinimumSize()) {
                    rectanglesWithWantedSize.add(wantedSizeRectangle)
                }
            }
        }
        assert(rectanglesWithWantedSize.isNotEmpty()) { "at least one rectangle must fit into the minimum sized rectangle" }
        return rectanglesWithWantedSize
    }

}