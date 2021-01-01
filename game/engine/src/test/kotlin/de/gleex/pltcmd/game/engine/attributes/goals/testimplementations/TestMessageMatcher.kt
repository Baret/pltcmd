package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.hexworks.amethyst.api.Message
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Checks that the given [Maybe] is not empty and contains a [TestMessage] with the [expectedValue].
 *
 * @see haveValue
 */
fun haveContainedValue(expectedValue: Int) = object : Matcher<Maybe<Message<GameContext>>> {
    override fun test(maybe: Maybe<Message<GameContext>>): MatcherResult {
        val isNotEmpty = beEmptyMaybe()
                .invert()
                .test(maybe)
        return if (isNotEmpty.passed()) {
            haveValue(expectedValue).test(maybe.get())
        } else {
            isNotEmpty
        }
    }
}

/**
 * Checks that the message to test is a [TestMessage] that contains [expectedValue].
 *
 * You can also test a [TestMessage] wrapped into a [Maybe] with [haveContainedValue].
 */
fun haveValue(expectedValue: Int) = object : Matcher<Message<GameContext>> {
    override fun test(message: Message<GameContext>): MatcherResult {
        val correctType = MatcherResult.Companion.invoke(
                message is TestMessage,
                "$message should be instance of ${TestMessage::class}",
                "$message should not be an instance of ${TestMessage::class}"
        )
        return if (correctType.passed()) {
            MatcherResult.Companion.invoke(
                    (message as TestMessage).value == expectedValue,
                    "$message should contain value $expectedValue",
                    "$message should not contain $expectedValue"
            )
        } else {
            correctType
        }
    }
}