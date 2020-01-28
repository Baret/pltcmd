package de.gleex.pltcmd.model.world

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class CoordinateTest {

    val testCoordinate: Coordinate = Coordinate(123, 345)

    @Test
    fun toMainCoordinate() {
        val expected = MainCoordinate(1, 3)
        assertEquals(expected, testCoordinate.toMainCoordinate())
    }

    @Test
    fun withRelativeEasting() {
        val delta = 789
        val expected = Coordinate(testCoordinate.eastingFromLeft + delta, testCoordinate.northingFromBottom)
        assertEquals(expected, testCoordinate.withRelativeEasting(delta))
    }

    @Test
    fun withRelativeNorthing() {
        val delta = 789
        val expected = Coordinate(testCoordinate.eastingFromLeft, testCoordinate.northingFromBottom + delta)
        assertEquals(expected, testCoordinate.withRelativeNorthing(delta))
    }

    @Test
    fun withNegtiveRelativeEasting() {
        val delta = -19
        val expected = Coordinate(testCoordinate.eastingFromLeft + delta, testCoordinate.northingFromBottom)
        val withRelativeEasting = testCoordinate.withRelativeEasting(delta)
        assertEquals(expected, withRelativeEasting)
        assertTrue("New easting should be bigger") { withRelativeEasting.eastingFromLeft < testCoordinate.eastingFromLeft }
    }

    @Test
    fun withNegativeRelativeNorthing() {
        val delta = -198549
        val expected = Coordinate(testCoordinate.eastingFromLeft, testCoordinate.northingFromBottom + delta)
        val withRelativeNorthing = testCoordinate.withRelativeNorthing(delta)
        assertEquals(expected, withRelativeNorthing)
        assertTrue("New northing should be lower") { withRelativeNorthing.northingFromBottom < testCoordinate.northingFromBottom }
    }

    @Test
    fun compareTo() {
        val otherCoordinate = Coordinate(124, 344)
        testCompareTo(otherCoordinate, testCoordinate)
        // proof KDoc
        val c13 = Coordinate(1,3)
        val c22 = Coordinate(2,2)
        val c32 = Coordinate(3,2)
        testCompareTo(c22, c32)
        testCompareTo(c32, c13)
        testCompareTo(c22, c13)
    }

    private fun testCompareTo(lesserCoordinate: Coordinate, majorCoordinate: Coordinate) {
        assertEquals(0, lesserCoordinate.compareTo(lesserCoordinate))
        assertEquals(0, majorCoordinate.compareTo(majorCoordinate))
        assertEquals(-1, lesserCoordinate.compareTo(majorCoordinate))
        assertEquals(1, majorCoordinate.compareTo(lesserCoordinate))
    }

}