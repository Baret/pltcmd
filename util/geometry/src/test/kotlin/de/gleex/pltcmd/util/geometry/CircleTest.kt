package de.gleex.pltcmd.util.geometry

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt

class CircleTest : StringSpec({
    val result = mutableListOf<Pair<Int, Int>>()
    val resultCatcher: (Int, Int) -> Unit = { x, y -> result.add(Pair(x, y)) }
    val center = Pair(13, 17)

    "circleWithRadius 0" {
        result.clear()
        circleWithRadius(center.first, center.second, 0, resultCatcher)
        result shouldBe listOf(center)
    }

    "circleWithRadius 1" {
        result.clear()
        circleWithRadius(center.first, center.second, 1, resultCatcher)
        result.size shouldBe 9
        // square with edge length 3
        for (x in (center.first - 1)..(center.first + 1)) {
            for (y in (center.second - 1)..(center.second + 1)) {
                result shouldContain Pair(x, y)
            }
        }
    }

    "circleWithRadius 5" {
        result.clear()
        circleWithRadius(center.first, center.second, 5, resultCatcher)
        result.size shouldBe 97
        // check distance for each point
        result.forAll { it.distanceTo(center) shouldBeLessThanOrEqual 5.5 }
    }

})

fun Pair<Int, Int>.distanceTo(other: Pair<Int, Int>): Double {
    val dx = first - other.first
    val dy = second - other.second
    return sqrt((dx * dx + dy * dy).toDouble())
}