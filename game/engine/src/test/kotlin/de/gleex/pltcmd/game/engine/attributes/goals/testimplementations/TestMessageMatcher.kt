package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.hexworks.amethyst.api.Message

/**
 * Checks that the message to test is a [TestMessage] that contains [expectedValue].
 */
fun haveValue(expectedValue: Int) = object : Matcher<Message<GameContext>> {
    override fun test(value: Message<GameContext>): MatcherResult {
        val correctType = MatcherResult(
            value is TestMessage,
            { "$value should be instance of ${TestMessage::class}" },
            { "$value should not be an instance of ${TestMessage::class}" }
        )
        return if (correctType.passed()) {
            MatcherResult(
                (value as TestMessage).value == expectedValue,
                { "$value should contain value $expectedValue" },
                {
                    "$value should not contain $expectedValue"
                }
            )
        } else {
            correctType
        }
    }
}