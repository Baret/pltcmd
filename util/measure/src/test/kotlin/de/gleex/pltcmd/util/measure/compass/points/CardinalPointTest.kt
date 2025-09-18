package de.gleex.pltcmd.util.measure.compass.points

import de.gleex.pltcmd.util.measure.compass.bearing.Bearing
import de.gleex.pltcmd.util.measure.compass.points.CardinalPoint.*
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class CardinalPointTest : WordSpec({
    "The cardinal point enum" should {
        "be sorted clock-wise, starting with north" {
            val expectedOrder = listOf(N, NE, E, SE, S, SW, W, NW).iterator()
            var expectedAngle = 0
            values().forAll {
                it shouldBe expectedOrder.next()
                it.angle shouldBe expectedAngle
                expectedAngle += 45
            }
        }
    }

    "Turning a bearing into a cardinal point" should {
        "result in the closest cardinal point" {
            val anglesToCheck: MutableSet<Int> = (0..359).toMutableSet()
            forAll(
                row(338, 359, N),
                row(0, 22, N),
                row(23, 67, NE),
                row(68, 112, E),
                row(113, 157, SE),
                row(158, 202, S),
                row(203, 247, SW),
                row(248, 292, W),
                row(293, 337, NW)
            ) { fromAngle, toAngle, expectedCardinal ->
                (fromAngle..toAngle).forEach { angle ->
                    angle.asClue {
                        CardinalPoint.fromBearing(Bearing(angle)) shouldBe expectedCardinal
                        anglesToCheck.remove(angle)
                    }
                }
            }
            anglesToCheck shouldBe emptySet()
        }
    }
})