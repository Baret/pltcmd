package de.gleex.pltcmd.game.ui.strings

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.databinding.api.extension.toProperty

class DefaultFrontendStringTest : WordSpec({
    val testString = "A very long string that needs to be truncated."

    "The default implementation of a frontendString" should {
        FrontendString.Format.values()
                .forEach { length ->
                    "truncate correctly for length $length" {
                        val actualValue = DefaultFrontendString(testString, length).value
                        when (length) {
                            FrontendString.Format.FULL    -> actualValue shouldBe testString
                            FrontendString.Format.SIDEBAR -> actualValue shouldBe "A very long string that needs to..."
                            FrontendString.Format.SHORT5  -> actualValue shouldBe "A ver"
                            FrontendString.Format.SHORT3  -> actualValue shouldBe "A v"
                            FrontendString.Format.ICON    -> actualValue shouldBe "A"
                        }
                    }
                }

        "be observable" {
            val observableString = testString.toProperty()
            val frontendString = DefaultFrontendString(observableString)
            var observed = false

            frontendString.onChange {
                observed = true
            }

            frontendString.value shouldBe testString
            observed shouldBe false

            observableString.updateValue("new Value")

            frontendString.value shouldBe "new Value"
            observed shouldBe true
        }
    }

    "Adding two short strings" should {
        "result in a full combination of both" {
            val a = "a "
            val b = "b"
            val combined = DefaultFrontendString(a, FrontendString.Format.SHORT5) + DefaultFrontendString(b, FrontendString.Format.SHORT5)
            combined.value shouldBe "a b"
        }
    }

    "Adding two strings with different length" should {
        "result in a string with the shorter one" {
            val a = "one"
            val b = " two"
            val combined = DefaultFrontendString(a, FrontendString.Format.SHORT5) + DefaultFrontendString(b, FrontendString.Format.SIDEBAR)
            combined.value shouldBe "one t"
        }
    }
})
