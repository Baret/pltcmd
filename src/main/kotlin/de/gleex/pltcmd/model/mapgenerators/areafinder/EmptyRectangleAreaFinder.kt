package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateArea
import de.gleex.pltcmd.model.world.CoordinateRectangle
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Groups the empty spaces of a world into rectangles. The rectangles are as wide as possible like lines.
 */
class EmptyRectangleAreaFinder : AreaFinder {
    companion object {
        private val LOG = LoggerFactory.getLogger(EmptyRectangleAreaFinder::class)
    }

    override fun findAll(partialWorld: MutableWorld): Set<CoordinateArea> {
        val rectangles = mutableSetOf<CoordinateRectangle>()
        val allEmpty = partialWorld.findEmpty()
                .sorted()
                .toMutableList()
        while (allEmpty.isNotEmpty()) {
            val start = allEmpty.removeAt(0)
            val rectangle = getEmptyRectangle(start, allEmpty)
            allEmpty.removeAll(rectangle.toSet())
            rectangles.add(rectangle)
        }
        LOG.debug("Empty rectangles found in world: $rectangles")
        return rectangles
    }

    private fun getEmptyRectangle(start: Coordinate, allEmpty: Collection<Coordinate>): CoordinateRectangle {
        // prefer horizontal connected tiles (lines)
        var width = 1
        while (start.withRelativeEasting(width) in allEmpty) {
            width++
        }
        // check all vertical tiles (lines of same length)
        var height = 1
        while (isEmptyLine(start.withRelativeNorthing(height), width, allEmpty)) {
            height++
        }
        val end = start.withRelativeEasting(width - 1)
                .withRelativeNorthing(height - 1)
        LOG.trace("Found rectangle from $start to $end ($width x $height)")
        return CoordinateRectangle(start, end)
    }

    private fun isEmptyLine(lineStart: Coordinate, width: Int, allEmpty: Collection<Coordinate>): Boolean {
        val line = CoordinateArea(lineStart..lineStart.withRelativeEasting(width - 1))
        return allEmpty.containsAll(line.toSet())
    }

}