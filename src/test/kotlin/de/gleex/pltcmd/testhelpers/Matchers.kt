package de.gleex.pltcmd.testhelpers

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
        var errorMessage = ""
        var isSame = true
        val firstFail: WorldTile? = null

        if(value.origin != expected.origin) {
            errorMessage = "origin ${value.origin} does not equal expected ${expected.origin}"
            isSame = false
        } else if(value.size != expected.size) {
            errorMessage = "size of ${value.size} does not equal expected size of ${expected.size}"
            isSame = false
        } else {
            // check all tiles
        }

        return MatcherResult.Companion.invoke(
                isSame,
                "world map should have the same terrain but $errorMessage",
                "world map should not have the same terrain"
        )
    }

}
