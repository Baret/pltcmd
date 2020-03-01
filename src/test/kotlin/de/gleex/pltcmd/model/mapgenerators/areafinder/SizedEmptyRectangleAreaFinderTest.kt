package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateRectangle
import de.gleex.pltcmd.model.world.Sector
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class SizedEmptyRectangleAreaFinderTest : StringSpec({
    val origin = Coordinate(0, 950)
    val emptyWorld = MutableWorld(origin)

    "empty world must contain a maximum size rectangle and three remainders" {
        val minWidth = 2
        val minHeight = 3
        val maxWidth = 43
        val maxHeight = 47
        val largeTest = SizedEmptyRectangleAreaFinder(minWidth, minHeight, maxWidth, maxHeight)
        largeTest.findAll(emptyWorld) shouldBe setOf(
                CoordinateRectangle(
                        origin,
                        origin.withRelativeEasting(maxWidth - 1).withRelativeNorthing(maxHeight - 1)),
                CoordinateRectangle(
                        origin.withRelativeEasting(maxWidth),
                        origin.withRelativeEasting(Sector.TILE_COUNT - 1).withRelativeNorthing(maxHeight - 1)),
                CoordinateRectangle(
                        origin.withRelativeNorthing(maxHeight),
                        origin.withRelativeEasting(maxWidth - 1).withRelativeNorthing(Sector.TILE_COUNT - 1)),
                CoordinateRectangle(
                        origin.withRelativeEasting(maxWidth).withRelativeNorthing(maxHeight),
                        origin.withRelativeEasting(Sector.TILE_COUNT - 1).withRelativeNorthing(Sector.TILE_COUNT - 1))
        )
    }

    "empty world must contain all coordinates as 1x1 rectangle" {
        val minimumSizeTest = SizedEmptyRectangleAreaFinder(1, 1, 1, 1)
        val everyCoordinateAsRectangle = emptyWorld.findEmpty()
                .map { CoordinateRectangle(it, it) }
                .toSet()
        minimumSizeTest.findAll(emptyWorld) shouldBe everyCoordinateAsRectangle
    }
})