package de.gleex.pltcmd.game.ui.strings

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DefaultFrontendStringTest : WordSpec({
    val testString = "A very long string that needs to be truncated."

    "The default implementation of a frontendString" should {
        FrontendString.Length.values()
                .forEach { length ->
                    "truncate correctly for length $length" {
                        val actualValue = DefaultFrontendString(testString, length).value
                        when (length) {
                            FrontendString.Length.FULL -> actualValue shouldBe testString
                            FrontendString.Length.SIDEBAR -> actualValue shouldBe "A very long string that needs to b..."
                            FrontendString.Length.SHORT5 -> actualValue shouldBe "A ver"
                            FrontendString.Length.SHORT3 -> actualValue shouldBe "A v"
                            FrontendString.Length.ICON -> actualValue shouldBe "A"
                        }
                    }
                }
    }
})
