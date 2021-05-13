package de.gleex.pltcmd.util.measure.compass.points

import de.gleex.pltcmd.util.measure.compass.points.CardinalPoint.*
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class CardinalPointTest: WordSpec({
    "The cardinal point enum" should {
        "be sorted clock-wise, starting with north" {
            val expectedOrder = listOf(N, NE, E, SE, S, SW, W, NW).iterator()
            var expectedAngle = 0
            values().forAll {
                it shouldBe expectedOrder.next()
                it.bearing.value shouldBe expectedAngle
                expectedAngle += 45
            }
        }
    }
})