package de.gleex.pltcmd.model.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateRectangle
import de.gleex.pltcmd.model.world.Sector
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class SizedEmptyRectangleAreaFinderTest : StringSpec({
    val origin = Coordinate(0, 950)
    val worldWidth = Sector.TILE_COUNT
    val worldHeight = Sector.TILE_COUNT
    val emptyWorld = MutableWorld(origin, worldWidth, worldHeight)

    "empty world must contain a maximum size rectangle and three remainders" {
        val minWidth = 2
        val minHeight = 3
        val maxWidth = 43
        val maxHeight = 47
        val largeTest = SizedEmptyRectangleAreaFinder(minWidth, minHeight, maxWidth, maxHeight)
        largeTest.findAll(emptyWorld) shouldBe setOf(
                CoordinateRectangle(
                        origin,
                        origin.movedBy(maxWidth - 1, maxHeight - 1)),
                CoordinateRectangle(
                        origin.withRelativeEasting(maxWidth),
                        origin.movedBy(worldWidth - 1, maxHeight - 1)),
                CoordinateRectangle(
                        origin.withRelativeNorthing(maxHeight),
                        origin.movedBy(maxWidth - 1, worldHeight - 1)),
                CoordinateRectangle(
                        origin.withRelativeEasting(maxWidth).withRelativeNorthing(maxHeight),
                        origin.movedBy(worldWidth - 1, worldHeight - 1))
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