package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateRectangle
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Groups the empty spaces of a world into rectangles. The rectangles are as wide as possible like lines.
 */
class EmptyRectangleAreaFinder : AreaFinder {
    companion object {
        private val LOG = LoggerFactory.getLogger(EmptyRectangleAreaFinder::class)
    }

    override fun findAll(partialWorld: MutableWorld): Set<CoordinateRectangle> {
        val start = System.currentTimeMillis()
        val rectangles = mutableSetOf<CoordinateRectangle>()
        val allEmpty = partialWorld.findEmpty()
                .sorted()
                .toMutableList()
        while (allEmpty.isNotEmpty()) {
            val rectangle = getEmptyRectangle(allEmpty)
            allEmpty.removeAll(rectangle.toSet())
            rectangles.add(rectangle)
        }
        val duration = System.currentTimeMillis() - start
        LOG.debug("Found ${rectangles.size} empty rectangles in $duration ms")
        return rectangles
    }

    private fun getEmptyRectangle(followingEmpty: List<Coordinate>): CoordinateRectangle {
//        val startTime = System.currentTimeMillis()
        val start = followingEmpty[0]
        // prefer horizontal connected tiles (lines)
        val width = getConnectedLineLength(start, followingEmpty)
        // check all vertical tiles (lines of same length)
        var height = 1
        if (width > 1) {
            while (getConnectedLineLength(start.withRelativeNorthing(height), followingEmpty) == width) {
                height++
            }
        }
        val end = start.withRelativeEasting(width - 1)
                .withRelativeNorthing(height - 1)
//        val duration = System.currentTimeMillis() - startTime
//        LOG.debug("Found rectangle from $start to $end ($width x $height) in $duration ms or ${duration / (width * height)} ms per tile")
        return CoordinateRectangle(start, end)
    }

    private fun getConnectedLineLength(start: Coordinate, followingEmpty: List<Coordinate>): Int {
        val startIndex = followingEmpty.indexOf(start)
        if (startIndex == -1) {
            // element behind all empty coordinates
            return 0
        }
        var indexToCheck = startIndex + 1
        var previous = start
        while (indexToCheck < followingEmpty.size) {
            val next = followingEmpty[indexToCheck]
            if (next.followsHorizontally(previous)) {
                indexToCheck++
                previous = next
            } else {
                break
            }
        }
        return indexToCheck - startIndex
    }

    private fun Coordinate.followsHorizontally(previous: Coordinate): Boolean {
        return previous.eastingFromLeft + 1 == eastingFromLeft
    }

}
