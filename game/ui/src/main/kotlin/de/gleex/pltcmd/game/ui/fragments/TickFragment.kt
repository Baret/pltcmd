package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import org.hexworks.zircon.api.Components

/**
 * Displays the current game time.
 */
class TickFragment(override val width: Int) : BaseFragment {
    companion object {
        const val FRAGMENT_HEIGHT = 1
    }

    override val root = Components.
                            vbox().
                            withSize(width, FRAGMENT_HEIGHT).
                            build().
                            apply {
                                addComponent(Components.
                                    header().
                                    withSize(contentSize.width, 1).
                                    build().
                                    apply {
                                        withFrontendString(
                                                Format.SIDEBAR,
                                                "Day ", Ticker.currentDay, ", ", Ticker.currentTimeString, " (", Ticker.currentTickObservable, ")")
                                    })
                            }
}