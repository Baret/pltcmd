package de.gleex.pltcmd.model.world.testhelpers

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import mu.KotlinLogging

// - - - Matchers for WorldMap

private val log = KotlinLogging.logger {  }

infix fun WorldMap.shouldHaveSameTerrain(other: WorldMap) = this should haveSameTerrain(other)

fun haveSameTerrain(expected: WorldMap) = object: Matcher<WorldMap> {
    override fun test(value: WorldMap): MatcherResult {
        val sectors = value.sectors.sorted().toList()
        val expectedSectors = expected.sectors.sorted().toList()
        val errorMessage: String? = when {
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
                log.debug { "Checking if all tiles are the same" }
                val tilesAreTheSame = containExactly(expected.allTiles).test(value.allTiles).passed()
                log.debug { "Tiles are the same? $tilesAreTheSame" }
                if(!tilesAreTheSame) {
                    "tiles are not the same"
                } else {
                    null
                }
            }
        }

        log.debug { "Done matching world maps. Errormessage: '$errorMessage'" }

        return MatcherResult(
            errorMessage == null,
            { "world map should have the same terrain but $errorMessage" },
            { "world map should not have the same terrain" }
        )
    }

}

private fun Sector.isNotEqualTo(otherSector: Sector) = this != otherSector

// - - -