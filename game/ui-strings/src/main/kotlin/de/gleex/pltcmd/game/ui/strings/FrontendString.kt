package de.gleex.pltcmd.game.ui.strings

import de.gleex.pltcmd.game.options.UiOptions
import org.hexworks.cobalt.databinding.api.value.ObservableValue


/**
 * A string representation of an object that can be used in the frontend.
 *
 * It provides means to trim the string down to various lengths so it will always fit into the currently available
 * space (i.e. a sidebar or a dialog) while always representing a human readable resemblance of the object.
 */
interface FrontendString<out T : Any> : ObservableValue<String> {

    /**
     * The [Format] restricting the space available for this string.
     */
    val format: Format

    /**
     * Creates a new [FrontendString] with both strings combined. This acts like a bindPlusWith call. The resulting
     * frontend string will have the shorter of both lengths. This means the resulting string will not be longer
     * than any one of the two.
     */
    operator fun plus(other: FrontendString<Any>): FrontendString<*>

    /**
     * The format of a [FrontendString] restricts its maximum [length].
     *
     * @param length the maximum length a [FrontendString] may use.
     */
    enum class Format(val length: Int) {
        /**
         * The minimum length of 1. When space is really really sparse ;)
         */
        ICON(1),

        /**
         * A very short string with length 3.
         */
        SHORT3(3),

        /**
         * A short string with length 5.
         */
        SHORT5(5),

        /**
         * A string that fits into a sidebar. The value is actually [UiOptions.SIDEBAR_WIDTH] - 2 to subtract
         * the border and get its content size.
         */
        SIDEBAR(UiOptions.SIDEBAR_WIDTH - 2),

        /**
         * With this format the string has no length restriction.
         */
        FULL(Int.MAX_VALUE)
    }
}