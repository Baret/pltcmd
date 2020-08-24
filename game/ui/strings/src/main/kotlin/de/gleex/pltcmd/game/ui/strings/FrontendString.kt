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

    val length: Length

    /**
     * The maximum length of a [FrontendString].
     */
    enum class Length(val characters: Int) {
        FULL(Int.MAX_VALUE),
        SIDEBAR(UiOptions.INTERFACE_PANEL_WIDTH),
        SHORT5(5),
        SHORT3(3),
        ICON(1)
    }
}