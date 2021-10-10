package de.gleex.pltcmd.model.mapgeneration.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import mu.KotlinLogging
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Groups the empty spaces of a world into rectangles. The rectangles are as wide as possible like lines.
 */
class EmptyRectangleAreaFinder : AreaFinder {

    override fun findAll(partialWorld: MutableWorld): Set<CoordinateRectangle> {
        val start = System.currentTimeMillis()
        val rectangles = mutableSetOf<CoordinateRectangle>()
        val allEmpty: SortedSet<Coordinate> = partialWorld.findEmpty()
        while (allEmpty.isNotEmpty()) {
            val rectangle = getEmptyRectangle(allEmpty.iterator())
            allEmpty.removeAll(rectangle.toSet())
            rectangles.add(rectangle)
        }
        val duration = System.currentTimeMillis() - start
        log.debug { "Found ${rectangles.size} empty rectangles in $duration ms" }
        return rectangles
    }

    private fun getEmptyRectangle(followingEmpty: Iterator<Coordinate>): CoordinateRectangle {
        val start = followingEmpty.next()
        // prefer horizontal connected tiles (lines)
        val (emptyLineLength, notConnectedElementFromIterator) = getConnectedLineLength(start, followingEmpty)
        val rectWidth = emptyLineLength + 1 // 1 for start
        val rectHeight = getHeight(start, notConnectedElementFromIterator, followingEmpty, rectWidth)
        return CoordinateRectangle(start, rectWidth, rectHeight)
    }

    // check all vertical tiles (lines of same length)
    private fun getHeight(start: Coordinate, notConnectedElementFromIterator: Coordinate?, followingEmpty: Iterator<Coordinate>, rectWidth: Int): Int {
        var lastChecked = notConnectedElementFromIterator
        var lineLengthResult: Pair<Int, Coordinate?>
        var emptyLineLength: Int
        var height = 0
        do {
            height++
            val firstInVerticalLine = start.withRelativeNorthing(height)

            // skip elements in previous line to get to the first element in the next line
            while (firstInVerticalLine != lastChecked && followingEmpty.hasNext()) {
                lastChecked = followingEmpty.next()
            }

            if (firstInVerticalLine == lastChecked) {
                // the next line is connected to the start vertically, check how many coordinates are connected in that line
                lineLengthResult = getConnectedLineLength(lastChecked, followingEmpty)
                emptyLineLength = lineLengthResult.first + 1 // 1 for the firstInVerticalLine/lastChecked
                lastChecked = lineLengthResult.second
            } else {
                emptyLineLength = 0
            }
        } while (emptyLineLength >= rectWidth)
        return height
    }

    private fun getConnectedLineLength(start: Coordinate, followingEmpty: Iterator<Coordinate>): Pair<Int, Coordinate?> {
        var connected = 0
        var previousConnected = start
        var toCheck: Coordinate? = null
        while (followingEmpty.hasNext()) {
            toCheck = followingEmpty.next()
            if (toCheck.isEastNeighborOf(previousConnected)) {
                connected++
                previousConnected = toCheck
            } else {
                break
            }
        }
        return Pair(connected, toCheck)
    }

}
