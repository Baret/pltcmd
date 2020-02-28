package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.testhelpers.shouldBeExactly
import io.kotlintest.data.forall
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row

class SignalStrengthTest:WordSpec( {
    "Absolute radio signal power converted to a signal strength" should {
        "be 1.0 when > 100 and 0.0 when < ${RadioSignal.MIN_STRENGTH_THRESHOLD}" {
            forall(
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
                    row(10.000000001, 0.100000000),
                    row(1.000000001, 0.0),
                    row(0.0, 0.0),
                    row(-0.01, 0.0),
                    row(-0.0000000000000000000004, 0.0),
                    row(Double.MIN_VALUE, 0.0)
            ) { signalStrength, expectedPercent ->
                signalStrength.toSignalStrength() shouldBeExactly expectedPercent
            }
        }
    }
})