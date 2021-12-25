package de.gleex.pltcmd.model.mapgeneration.mapgenerators.data

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.shouldBe

class MutableWorldTest: WordSpec()
{
    private fun east(): Coordinate.() -> Coordinate = { this.withRelativeEasting(1) }
    private fun west(): Coordinate.() -> Coordinate = { this.withRelativeEasting(-1) }
    private fun north(): Coordinate.() -> Coordinate = { this.withRelativeNorthing(1) }
    private fun south(): Coordinate.() -> Coordinate = { this.withRelativeNorthing(-1) }
    private val allNeighbors = listOf(north(), east(), south(), west())
    private val westBorder = listOf(north(), east(), south())
    private val eastBorder = listOf(north(), west(), south())
    private val northBorder = listOf(west(), south(), east())
    private val southBorder = listOf(west(), north(), east())

    init {
        val world = MutableWorld()
        "A world from ${world.bottomLeftCoordinate} to ${world.topRightCoordinate} of size ${world.worldSizeWidthInTiles} by ${world.worldSizeHeightInTiles} tiles" should {
            forAll(
                    // SW corner
                    row(0, 0, listOf(east(), north())),
                    row(0, 1, westBorder),
                    row(1, 0, southBorder),
                    row(1, 1, allNeighbors),

                    // NE corner
                    row(49, 49, listOf(west(), south())),
                    row(49, 48, eastBorder),
                    row(48, 49, northBorder),
                    row(48, 48, allNeighbors),

                    // Some inside
                    row(2, 2, allNeighbors),
                    row(42, 13, allNeighbors),
                    row(49, 23, eastBorder),
                    row(9, 19, allNeighbors),

                    // some outside
                    row(-1, 0, emptyList()),
                    row(1, 50, emptyList()),
                    row(374, 983, emptyList()),
                    row(Int.MAX_VALUE, Int.MAX_VALUE, emptyList()),
                    row(Int.MIN_VALUE, Int.MIN_VALUE, emptyList()),
                    row(50, -1, emptyList())
            ) { easting, northing, neighborGenerators ->
                val c = Coordinate(easting, northing)
                val validNeighbors = neighborGenerators.map {
                    it.invoke(c)
                }
                "have ${validNeighbors.size} neighbors at $c" {
                    world.neighborsOf(c) shouldContainExactlyInAnyOrder validNeighbors
                }
            }
            "find no present coordinates" {
                world.find() shouldHaveSize 0
            }
            "find all coordinates as empty" {
                val emptyCoordinates = world.findEmpty()
                emptyCoordinates shouldBe CoordinateRectangle(world.bottomLeftCoordinate, world.topRightCoordinate).asSequence()
                emptyCoordinates shouldHaveCount world.worldSizeWidthInTiles * world.worldSizeHeightInTiles
            }
        }
    }
}
