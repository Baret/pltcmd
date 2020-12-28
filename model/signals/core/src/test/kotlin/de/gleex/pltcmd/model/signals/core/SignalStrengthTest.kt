package de.gleex.pltcmd.model.signals.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row

class SignalStrengthTest : WordSpec({
    "Invalid values" should {
        "not be allowed" {
            forAll(
                    row(Double.POSITIVE_INFINITY),
                    row(Double.MAX_VALUE),
                    row(1.1),
                    row(1.00000000000001),
                    row(-1.0),
                    row(-0.1),
                    row(-0.0000000000001),
                    row(-Double.MIN_VALUE),
                    row(-1.0 * Double.MAX_VALUE),
                    row(Double.NEGATIVE_INFINITY)
            ) { invalidValue ->
                shouldThrow<IllegalArgumentException> { SignalStrength(invalidValue) }
            }
        }
    }
})