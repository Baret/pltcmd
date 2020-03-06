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
                .toSortedSet()
        while (allEmpty.isNotEmpty()) {
            val rectangle = getEmptyRectangle(allEmpty.iterator())
            allEmpty.removeAll(rectangle.toSet())
            rectangles.add(rectangle)
        }
        val duration = System.currentTimeMillis() - start
        LOG.debug("Found ${rectangles.size} empty rectangles in $duration ms")
        return rectangles
    }

    private fun getEmptyRectangle(followingEmpty: Iterator<Coordinate>): CoordinateRectangle {
//        val startTime = System.currentTimeMillis()
        val start = followingEmpty.next()
        // prefer horizontal connected tiles (lines)
        var lineLengthResult = getConnectedLineLength(start, followingEmpty)
        var emptyLineLength = lineLengthResult.first
        var emptyBehindLine = lineLengthResult.second
        val rectWidth = emptyLineLength + 1 // 1 for start
        val rectHeight = getHeight(start, emptyBehindLine, followingEmpty, rectWidth)
        val end = start.withRelativeEasting(rectWidth - 1)
                .withRelativeNorthing(rectHeight - 1)
//        val duration = System.currentTimeMillis() - startTime
//        LOG.debug("Found rectangle from $start to $end ($rectWidth x $height) in $duration ms or ${duration / (rectWidth * height)} ms per tile")
        return CoordinateRectangle(start, end)
    }

    // check all vertical tiles (lines of same length)
    private fun getHeight(start: Coordinate, emptyBehindLine: Coordinate?, followingEmpty: Iterator<Coordinate>, rectWidth: Int): Int {
        var emptyAfterConnected = emptyBehindLine
        var lineLengthResult: Pair<Int, Coordinate?>
        var emptyLineLength: Int
        var height = 0 // will be increased in `do`
        do {
            height++
            val firstInVerticalLine = start.withRelativeNorthing(height)

            // skip elements in line to get to the next line
            while (firstInVerticalLine != emptyAfterConnected && followingEmpty.hasNext()) {
                emptyAfterConnected = followingEmpty.next()
            }

            if (firstInVerticalLine == emptyAfterConnected) {
                // next line must start with an empty coordinate
                lineLengthResult = getConnectedLineLength(emptyAfterConnected, followingEmpty)
                emptyLineLength = lineLengthResult.first + 1 // 1 for the emptyAfterConnected which was already iterated
                emptyAfterConnected = lineLengthResult.second
            } else {
                emptyLineLength = 0
            }
        } while (emptyLineLength >= rectWidth)
        return height
    }

    private fun getConnectedLineLength(start: Coordinate, followingEmpty: Iterator<Coordinate>): Pair<Int, Coordinate?> {
        var connected = 0
        var previous = start
        var next: Coordinate? = null
        while (followingEmpty.hasNext()) {
            next = followingEmpty.next()
            if (next.followsHorizontally(previous)) {
                connected++
                previous = next
            } else {
                break
            }
        }
        return Pair<Int, Coordinate?>(connected, next)
    }

    private fun Coordinate.followsHorizontally(previous: Coordinate): Boolean {
        return previous.eastingFromLeft + 1 == eastingFromLeft
    }

}
