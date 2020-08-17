package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Checks that the given [Maybe] is not empty and contains a [TestCommand] with the [expectedValue].
 *
 * @see haveValue
 */
fun haveContainedValue(expectedValue: Int) = object : Matcher<Maybe<Command<*, GameContext>>> {
    override fun test(maybe: Maybe<Command<*, GameContext>>): MatcherResult {
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
 * Checks that the command to test is a [TestCommand] that contains [expectedValue].
 *
 * You can also test a [TestCommand] wrapped into a [Maybe] with [haveContainedValue].
 */
fun haveValue(expectedValue: Int) = object : Matcher<Command<*, GameContext>> {
    override fun test(command: Command<*, GameContext>): MatcherResult {
        val correctType = MatcherResult.Companion.invoke(
                command is TestCommand,
                "$command should be instance of ${TestCommand::class}",
                "$command should not be an instance of ${TestCommand::class}"
        )
        return if (correctType.passed()) {
            MatcherResult.Companion.invoke(
                    (command as TestCommand).value == expectedValue,
                    "$command should contain value $expectedValue",
                    "$command should not contain $expectedValue"
            )
        } else {
            correctType
        }
    }
}