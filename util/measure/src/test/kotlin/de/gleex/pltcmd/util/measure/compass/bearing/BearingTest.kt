package de.gleex.pltcmd.util.measure.compass.bearing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class BearingTest: WordSpec({
    "A valid bearing" should {
        "have a value between 0 and 359" {
            for(i in 0..359) {
                Bearing(i).value shouldBe i
            }
            for (i in -400..-1) {
                shouldThrow<IllegalArgumentException> {
                    Bearing(i)
                }
            }
            for (i in 360..500) {
                shouldThrow<IllegalArgumentException> {
                    Bearing(i)
                }
            }
        }
    }
})