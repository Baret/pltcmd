package de.gleex.pltcmd.game.ui.strings

import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A string representation of an object that can be used in the frontend.
 *
 * It provides means to trim the string down to various lengths so it will always fit into the currently available
 * space (i.e. a sidebar or a dialog) while always representing a human readable resemblance of the object.
 */
interface FrontendString {
    /**
     * The original string, unchanged and untrimmed. Possibly not reliably usable in the frontend.
     */
    val original: ObservableValue<String>

    /**
     * This length fits into a sidebar.
     *
     * @see de.gleex.pltcmd.game.options.UiOptions.INTERFACE_PANEL_WIDTH
     */
    val sidebar: String
}