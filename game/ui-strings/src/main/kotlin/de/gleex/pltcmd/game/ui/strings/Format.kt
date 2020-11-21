package de.gleex.pltcmd.game.ui.strings

import de.gleex.pltcmd.game.options.UiOptions

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
     * A string that fits into a sidebar. The value is actually [UiOptions.INTERFACE_PANEL_WIDTH] - 2 to subtract
     * the border and get its content size.
     */
    SIDEBAR(UiOptions.SIDEBAR_WIDTH - 2),

    /**
     * With this format the string has no length restriction.
     */
    FULL(Int.MAX_VALUE)
}

/**
 * Returns the shorter of both formats.
 */
fun minOf(first: Format, second: Format) =
        if (first.length <= second.length) {
            first
        } else {
            second
        }