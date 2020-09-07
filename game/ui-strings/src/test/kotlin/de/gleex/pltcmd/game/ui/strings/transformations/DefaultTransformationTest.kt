package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DefaultTransformationTest : WordSpec({
    val testString = "A very long string that needs to be truncated."

    "The default transformation for frontend strings" should {
        Format.values()
                .forEach { format ->
                    "truncate correctly for format $format" {
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
    }
})
