package de.gleex.pltcmd.model.world.testhelpers

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import org.hexworks.cobalt.databinding.api.extension.fold

// - - - Matchers for WorldMap

infix fun WorldMap.shouldHaveSameTerrain(other: WorldMap) = this should haveSameTerrain(other)

fun haveSameTerrain(expected: WorldMap) = object: Matcher<WorldMap> {
    override fun test(value: WorldMap): MatcherResult {
        val sectors = value.sectors.sorted().toList()
        val expectedSectors = expected.sectors.sorted().toList()
        val errorMessage = when {
            value.origin != expected.origin -> {
                "origin ${value.origin} does not equal expected ${expected.origin}"
            }
            value.width != expected.width -> {
                "width of ${value.width} does not equal expected width of ${expected.width}"
            }
            value.height != expected.height -> {
                "height of ${value.height} does not equal expected height of ${expected.height}"
            }
            sectors.size != expectedSectors.size -> {
                "Number of sectors ${sectors.size} does not equal expected ${expectedSectors.size}"
            }
            else -> {
                // check all tiles
                sectors
                    .withIndex()
                    .firstOrNull { (index, sector) -> sector.isNotEqualTo(expectedSectors[index]) }
                    .fold(
                        whenNull = { null },
                        whenNotNull = { "Sector at ${it.value.origin} does not equal expected sector!" })
            }
        }

        return MatcherResult(
            errorMessage != null,
            { "world map should have the same terrain but $errorMessage" },
            { "world map should not have the same terrain" }
        )
    }

}

private fun Sector.isNotEqualTo(otherSector: Sector) = this != otherSector

// - - -