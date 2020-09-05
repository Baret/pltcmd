package de.gleex.pltcmd.game.ui.strings

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.databinding.api.extension.toProperty

class DefaultFrontendStringTest : WordSpec({
    val testString = "A very long string that needs to be truncated."

    "The default implementation of a frontend string" should {
        Format.values()
                .forEach { length ->
                    "truncate correctly for length $length" {
                        val actualValue = DefaultFrontendString(testString, length).value
                        when (length) {
                            Format.FULL    -> actualValue shouldBe testString
                            Format.SIDEBAR -> actualValue shouldBe "A very long string that needs to..."
                            Format.SHORT5  -> actualValue shouldBe "A ver"
                            Format.SHORT3  -> actualValue shouldBe "A v"
                            Format.ICON    -> actualValue shouldBe "A"
                        }
                    }
                }

        "be observable" {
            val observableString = testString.toProperty()
            val frontendString = DefaultFrontendString(observableString)
            var changed = false

            frontendString.onChange {
                changed = true
            }

            frontendString.value shouldBe testString
            changed shouldBe false

            observableString.updateValue("new Value")

            frontendString.value shouldBe "new Value"
            changed shouldBe true
        }
    }

    "Adding two short strings" should {
        "result in a full combination of both" {
            val a = "a "
            val b = "b"
            val combined = DefaultFrontendString(a, Format.SHORT5) + DefaultFrontendString(b, Format.SHORT5)
            combined.value shouldBe "a b"
        }

        "result in a truncated combination of both" {
            val a = "abcd"
            val b = "xyz!"
            val combined = DefaultFrontendString(a, Format.SHORT5) + DefaultFrontendString(b, Format.SHORT5)
            combined.value shouldBe "abcdx"
        }
    }

    "Adding two strings with different length" should {
        "result in a string with the shorter one" {
            val a = "one"
            val b = " two"
            val combined = DefaultFrontendString(a, Format.SHORT5) + DefaultFrontendString(b, Format.SIDEBAR)
            combined.value shouldBe "one t"
        }
    }
})
