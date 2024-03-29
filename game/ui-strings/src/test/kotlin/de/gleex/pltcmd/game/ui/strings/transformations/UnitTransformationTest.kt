package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.units.Units
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.beUnique
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beUpperCase
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class UnitTransformationTest : WordSpec() {

    init {
        "The unit transformation" should {
            "work for units and unit blueprints" {
                Format.entries.toTypedArray()
                        .forAll { format ->
                            Units.entries.toTypedArray()
                                    .forAll { blueprint ->
                                        val blueprintValue: String = blueprint.toFrontendString(format).value
                                        val unitValue: String = blueprint.new()
                                                .toFrontendString(format).value
                                        blueprintValue shouldBe unitValue
                                    }
                        }
            }
        }

        "The icon for a unit" should {
            Units.entries
                    .groupBy { it.kind }
                    .forEach { (unitKind, blueprints) ->
                        "be unique for unit kind $unitKind" {
                            val icons = blueprints.map { it.toFrontendString(Format.ICON).value }
                            icons.logDuplicates(Format.ICON)
                            icons should beUnique()
                        }
                    }
        }

        "The 3 digit frontend string of all units" should {
            val short3Strings = Units.entries
                    .map { it.toFrontendString(Format.SHORT3).value }
            "be unique" {
                short3Strings.logDuplicates(Format.SHORT3)
                short3Strings should beUnique()
            }

            "be upper case" {
                short3Strings.forAll {
                    it should beUpperCase()
                }
            }
        }
    }

    private fun Collection<String>.logDuplicates(format: Format) {
        val duplicates = this.duplicates()
        if(duplicates.isNotEmpty()) {
            log.warn { "Non unique $format values: $duplicates" }
        }
    }

    private fun Collection<String>.duplicates(): Set<String> {
        var prev = sorted().first()
        return sorted().drop(1).filter {
            val isDuplicate = it == prev
            prev = it
            isDuplicate
        }.toSet()
    }
}