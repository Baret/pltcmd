package de.gleex.pltcmd.model.world.testhelpers

import arrow.core.firstOrNone
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import org.hexworks.cobalt.datatypes.Maybe

// - - - Matchers for WorldMap

infix fun WorldMap.shouldHaveSameTerrain(other: WorldMap) = this should haveSameTerrain(other)

fun haveSameTerrain(expected: WorldMap) = object: Matcher<WorldMap> {
    override fun test(value: WorldMap): MatcherResult {
        val sectors = value.sectors.toList()
        val expectedSectors = expected.sectors.toList()
        val errorMessage = when {
            value.origin != expected.origin -> {
                Maybe.of("origin ${value.origin} does not equal expected ${expected.origin}")
            }
            value.width != expected.width   -> {
                Maybe.of("width of ${value.width} does not equal expected width of ${expected.width}")
            }
            value.height != expected.height -> {
                Maybe.of("height of ${value.height} does not equal expected height of ${expected.height}")
            }
            sectors.size != expectedSectors.size -> {
                Maybe.of("Number of sectors ${sectors.size} does not equal expected ${expectedSectors.size}")
            }
            else                            -> {
                // check all tiles
                sectors.
                    withIndex().
                    firstOrNone { (index, sector) -> sector.isNotEqualTo(expectedSectors[index]) }.fold(
                        { Maybe.empty<String>() },
                        { Maybe.of("Sector at ${it.value.origin} does not equal expected sector!") })
            }
        }

        return MatcherResult.Companion.invoke(
                errorMessage.isEmpty(),
                "world map should have the same terrain but ${errorMessage.orElse("")}",
                "world map should not have the same terrain"
        )
    }

}

private fun Sector.isNotEqualTo(otherSector: Sector) =
        if(this != otherSector) {
            true
        } else {
            // The coordinates are the same, but the terrain might differ
            tiles.
                firstOrNone { (coordinate, terrain) ->
                    val otherTerrain = otherSector.tiles.find { it.coordinate == coordinate }!!.terrain
                    otherTerrain != terrain
                }.
                isDefined()
        }

// - - -