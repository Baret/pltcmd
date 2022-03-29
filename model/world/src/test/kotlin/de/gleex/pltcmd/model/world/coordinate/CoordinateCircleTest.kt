package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.distance.times
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CoordinateCircleTest : StringSpec() {

    val center = Coordinate(12, 34)

    /**
     * Looks like:
     * ```
     *   08                      16
     * 38      ## ## ## ## ##
     *      ## ## ## ## ## ## ##
     *   ## ## ## ## ## ## ## ## ##
     *   ## ## ## ## ## ## ## ## ##
     *   ## ## ## ## ## ## ## ## ##
     *   ## ## ## ## ## ## ## ## ##
     *   ## ## ## ## ## ## ## ## ##
     *      ## ## ## ## ## ## ##
     * 30      ## ## ## ## ##
     * ```
     */
    val radiusInTiles = 4

    lateinit var underTest: CoordinateCircle

    init {

        beforeTest {
            underTest = CoordinateCircle(center, radiusInTiles * WorldTile.edgeLength)
        }

        "size" {
            // 7x7 + 5 on each side
            underTest.size shouldBe 69
        }

        "toSet" {
            val expectedCoordinates = mutableSetOf<Coordinate>()
            // square in circle
            val squareOffset: Int = radiusInTiles - 1
            val innerSquareEdgeLength = (2 * radiusInTiles) - 1
            for (x in 0 until innerSquareEdgeLength) {
                for (y in 0 until innerSquareEdgeLength) {
                    expectedCoordinates += center.movedBy(-squareOffset + x, -squareOffset + y)
                }
            }
            // edges
            val edgeOffset: Int = radiusInTiles / 2
            val edgeLength = radiusInTiles / 2 + 1
            for (i in 0 until edgeLength) {
                // top
                expectedCoordinates += CoordinatePath.line(
                    center.movedBy(-edgeOffset, -radiusInTiles),
                    center.movedBy(+edgeOffset, -radiusInTiles)
                )
                // right
                expectedCoordinates += CoordinatePath.line(
                    center.movedBy(+radiusInTiles, -edgeOffset),
                    center.movedBy(+radiusInTiles, +edgeOffset)
                )
                // bottom
                expectedCoordinates += CoordinatePath.line(
                    center.movedBy(-edgeOffset, +radiusInTiles),
                    center.movedBy(+edgeOffset, +radiusInTiles)
                )
                // left
                expectedCoordinates += CoordinatePath.line(
                    center.movedBy(-radiusInTiles, -edgeOffset),
                    center.movedBy(-radiusInTiles, +edgeOffset)
                )
            }

            underTest.toSet() shouldBe expectedCoordinates
        }

        "contains" {
            shouldContain(center)
            // just test edges
            // top
            shouldContain(center.withRelativeNorthing(radiusInTiles))
            shouldNotContain(center.withRelativeNorthing(radiusInTiles + 1))
            // right
            shouldContain(center.withRelativeEasting(radiusInTiles))
            shouldNotContain(center.withRelativeEasting(radiusInTiles + 1))
            // bottom
            shouldContain(center.withRelativeNorthing(-radiusInTiles))
            shouldNotContain(center.withRelativeNorthing(-radiusInTiles - 1))
            // left
            shouldContain(center.withRelativeEasting(-radiusInTiles))
            shouldNotContain(center.withRelativeEasting(-radiusInTiles - 1))
            // top left
            shouldNotContain(center.movedBy(-radiusInTiles, -radiusInTiles))
            shouldContain(center.movedBy(-radiusInTiles + 1, radiusInTiles - 1))
            // top right
            shouldNotContain(center.movedBy(radiusInTiles, -radiusInTiles))
            shouldContain(center.movedBy(radiusInTiles - 1, radiusInTiles - 1))
            // bottom right
            shouldNotContain(center.movedBy(radiusInTiles, radiusInTiles))
            shouldContain(center.movedBy(radiusInTiles - 1, -radiusInTiles + 1))
            // bottom left
            shouldNotContain(center.movedBy(-radiusInTiles, radiusInTiles))
            shouldContain(center.movedBy(-radiusInTiles + 1, -radiusInTiles + 1))
        }
    }

    private fun shouldContain(movedBy: Coordinate) {
        withClue("should contain $movedBy") {
            underTest.contains(movedBy) shouldBe true
        }
    }

    private fun shouldNotContain(movedBy: Coordinate) {
        withClue("should not contain $movedBy") {
            underTest.contains(movedBy) shouldBe false
        }
    }
}
