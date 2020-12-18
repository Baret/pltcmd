package de.gleex.pltcmd.model.signals.radio

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.shouldBeExactly

class RadioSignalPropagatorTest : WordSpec({
    "Absolute radio signal power converted to a signal strength" should {
        "be 1.0 when > 100 and 0.0 when < ${RadioSignalPropagator.MIN_POWER_THRESHOLD}" {
            forAll(
                    // Rounding precision is 5 digits
                    row(Double.MAX_VALUE, 1.0),
                    row(110.0, 1.0),
                    row(100.0, 1.0),
                    row(99.8997, 0.999),
                    row(90.0, 0.90),
                    row(80.0, 0.80),
                    row(71.987654321, 0.71988),
                    row(70.0, 0.70),
                    row(60.0, 0.60),
                    row(50.0, 0.50),
                    row(40.0, 0.40),
                    row(30.0, 0.30),
                    row(20.0, 0.20),
                    row(10.0, 0.10),
                    row(10.000000001, 0.10),
                    row(RadioSignalPropagator.MIN_POWER_THRESHOLD + 1.0E-5, RadioSignalPropagator.MIN_POWER_THRESHOLD / 100.0),
                    row(RadioSignalPropagator.MIN_POWER_THRESHOLD, RadioSignalPropagator.MIN_POWER_THRESHOLD / 100.0),
                    row(RadioSignalPropagator.MIN_POWER_THRESHOLD - 1.0E-5, 0.0),
                    row(1.000000001, 0.0),
                    row(0.0, 0.0),
                    row(-0.01, 0.0),
                    row(-0.0000000000000000000004, 0.0),
                    row(Double.MIN_VALUE, 0.0)
            ) { signalPower, expectedPercent ->
                signalPower.convertToSignalStrength().strength shouldBeExactly expectedPercent
            }
        }
    }
})