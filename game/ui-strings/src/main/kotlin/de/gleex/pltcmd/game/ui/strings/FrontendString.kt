package de.gleex.pltcmd.game.ui.strings

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

}