package de.gleex.pltcmd.model.radio.testhelpers

import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import io.kotest.matchers.doubles.shouldBeExactly

// - - - Matchers for SignalStrength

infix fun SignalStrength.shouldBeExactly(expected: Double) = this.strength shouldBeExactly expected

// - - -