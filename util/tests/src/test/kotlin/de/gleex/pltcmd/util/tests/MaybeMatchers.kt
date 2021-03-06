package de.gleex.pltcmd.util.tests

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import org.hexworks.cobalt.datatypes.Maybe

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

fun beEmptyMaybe() = object: Matcher<Maybe<out Any>> {
    override fun test(value: Maybe<out Any>): MatcherResult {
        return MatcherResult.Companion.invoke(
                value.isEmpty(),
                "$value should be empty",
                "$value should not be empty"
        )
    }
}
