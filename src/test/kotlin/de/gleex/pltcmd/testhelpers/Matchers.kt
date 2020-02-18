package de.gleex.pltcmd.testhelpers

import arrow.core.firstOrNone
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.should
import org.hexworks.cobalt.datatypes.Maybe

// - - - Matchers for Maybe

infix fun <T> Maybe<T>.shouldContainValue(expectedValue: T) = this should containValue(expectedValue)

fun <T> containValue(expectedValue: T) = object: Matcher<Maybe<T>> {
    override fun test(value: Maybe<T>): MatcherResult {
        return MatcherResult.invoke(
                value.isPresent && value.get() == expectedValue,
                "$value should contain value $expectedValue",
                "$value should not contain value $expectedValue"
        )
    }
}

fun beEmpty() = object: Matcher<Maybe<out Any>> {
    override fun test(value: Maybe<out Any>): MatcherResult {
        return MatcherResult.Companion.invoke(
                value.isEmpty(),
                "$value should be empty",
                "$value should not be empty"
        )
    }
}

// - - - Matchers for WorldMap

infix fun WorldMap.shouldHaveSameTerrain(other: WorldMap) = this should haveSameTerrain(other)

fun haveSameTerrain(expected: WorldMap) = object: Matcher<WorldMap> {
    override fun test(value: WorldMap): MatcherResult {
        var errorMessage = Maybe.empty<String>()
        val firstFail: WorldTile? = null

        val sectors = value.sectors.toList()
        val expectedSectors = expected.sectors.toList()
        errorMessage = when {
            value.origin != expected.origin -> {
                Maybe.of("origin ${value.origin} does not equal expected ${expected.origin}")
            }
            value.size != expected.size     -> {
                Maybe.of("size of ${value.size} does not equal expected size of ${expected.size}")
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