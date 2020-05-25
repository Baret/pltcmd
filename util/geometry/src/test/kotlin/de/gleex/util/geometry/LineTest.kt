package de.gleex.util.geometry

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class LineTest : WordSpec({
    val generated = mutableListOf<Pair<Int, Int>>()
    val generator = { x: Int, y: Int ->
        generated += Pair(x, y)
    }
    //    0 G  B  A
    //    9
    //    8
    //    7
    //    6
    //    5
    //    4
    //    3 F  S  C
    //    2
    //    1
    // 32101234567890
    //    1
    //    2
    //    3
    //    4 D  E  H
    val startX = 5
    val startY = 3
    val deltaX = 3
    val deltaY = 7
    "A line" should {
        forAll(
                // Test S is start only
                row(startX + 0, startY + 0, line(startX, startY)),
                // Test A to NE
                row(startX + deltaX, startY + deltaY, line(startX, startY, 5, 4, 6, 5, 6, 6, 7, 7, 7, 8, 8, 9, startX + deltaX, startY + deltaY)),
                // Test B to N
                row(startX + 0, startY + deltaY, line(startX, startY, startX, startY + 1, startX, startY + 2, startX, startY + 3, startX, startY + 4, startX, startY + 5, startX, startY + 6, startX, startY + deltaY)),
                // Test C to E
                row(startX + deltaX, startY + 0, line(startX, startY, startX + 1, startY, startX + 2, startY, startX + deltaX, startY)),
                // Test D to SW
                row(startX - deltaX, startY - deltaY, line(startX, startY, 5, 2, 4, 1, 4, 0, 3, -1, 3, -2, 2, -3, startX - deltaX, startY - deltaY)),
                // Test E to S
                row(startX - 0, startY - deltaY, line(startX, startY, startX, startY - 1, startX, startY - 2, startX, startY - 3, startX, startY - 4, startX, startY - 5, startX, startY - 6, startX, startY - deltaY)),
                // Test F to W
                row(startX - deltaX, startY - 0, line(startX, startY, startX - 1, startY, startX - 2, startY, startX - deltaX, startY)),
                // Test G to NW
                row(startX - deltaX, startY + deltaY, line(startX, startY, 5, 4, 4, 5, 4, 6, 3, 7, 3, 8, 2, 9, startX - deltaX, startY + deltaY)),
                // Test H to SE
                row(startX + deltaX, startY - deltaY, line(startX, startY, 5, 2, 6, 1, 6, 0, 7, -1, 7, -2, 8, -3, startX + deltaX, startY - deltaY))
        ) { toX, toY, expectedPoints ->
            "be correct when created from ($startX,$startY) to ($toX,$toY)" {
                generated.clear()
                pointsOfLine(startX, startY, toX, toY, generator)
                generated shouldBe expectedPoints
            }
        }
    }
})

fun line(vararg xy: Int): List<Pair<Int, Int>> {
    require(xy.size % 2 == 0)
    val result = mutableListOf<Pair<Int, Int>>()
    for (i in xy.indices step 2) {
        val x = xy[i]
        val y = xy[i + 1]
        result += Pair(x, y)
    }
    return result
}