package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class DefaultTransformationTest : WordSpec({
    val testString = "A very long string that needs to be truncated."

    "The default transformation for frontend strings" should {
        "truncate correctly" {
        Format.values()
                .forAll { format ->
                        val actualValue = testString.defaultTransformation(format)
                        when (format) {
                            Format.FULL    -> actualValue shouldBe testString
                            Format.SIDEBAR -> actualValue shouldBe "A very long string that needs to..."
                            Format.SHORT5  -> actualValue shouldBe "A ver"
                            Format.SHORT3  -> actualValue shouldBe "A v"
                            Format.ICON    -> actualValue shouldBe "A"
                        }
                    }
                }

        "call toString()" {
            val a = A(15)
            Format.values()
                    .forAll { format ->
                        val actualValue = a.defaultTransformation(format)
                        when (format) {
                            Format.FULL    -> actualValue shouldBe "A(foo=15)"
                            Format.SIDEBAR -> actualValue shouldBe "A(foo=15)"
                            Format.SHORT5  -> actualValue shouldBe "A(foo"
                            Format.SHORT3  -> actualValue shouldBe "A(f"
                            Format.ICON    -> actualValue shouldBe "A"
                        }
                    }
        }
    }
})

private data class A(val foo: Int)