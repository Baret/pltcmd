package de.gleex.pltcmd.game.ui.strings

import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.databinding.api.extension.toProperty

class FrontendStringTest : WordSpec({
    "A frontend string" should {
        "be observable" {
            val observableString = "some string".toProperty()
            val frontendString = observableString.toFrontendString()
            var changed = false

            frontendString.onChange {
                changed = true
            }

            frontendString.value shouldBe "some string"
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
            val combined = a.toFrontendString(Format.SHORT5) + b.toFrontendString(Format.SHORT5)
            combined.value shouldBe "a b"
        }

        "result in a truncated combination of both" {
            val a = "abcd"
            val b = "xyz!"
            val combined = a.toFrontendString(Format.SHORT5) + b.toFrontendString(Format.SHORT5)
            combined.value shouldBe "abcdx"
        }
    }

    "Adding two strings with different length" should {
        "result in a string with the shorter one" {
            val a = "one"
            val b = " two"
            val combined = a.toFrontendString(Format.SHORT5) + b.toFrontendString(Format.SIDEBAR)
            combined.value shouldBe "one t"
        }
    }

    "A transformation" should {
        "not result in a string not fitting the format" {
            val frontendStringWithIllegalTransformation =
                    FrontendString("foo".toProperty(), Format.SHORT5) {
                        "123456"
                    }
            shouldThrow<IllegalArgumentException> {
                frontendStringWithIllegalTransformation.value
            }
        }
    }
})