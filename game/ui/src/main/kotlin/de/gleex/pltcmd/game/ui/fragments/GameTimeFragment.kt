package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.graphics.Symbols

/**
 * Displays the current game time and provides buttons to pause/unpause the game.
 */
class GameTimeFragment(override val fragmentWidth: Int) : BaseFragment {
    companion object {
        const val FRAGMENT_HEIGHT = 2
        private const val BUTTON_ROW_SPACING = 2
    }

    override val root = Components.
                            vbox().
                            withSize(fragmentWidth, FRAGMENT_HEIGHT).
                            build().
                            apply {
                                addComponents(
                                        Components.
                                            header().
                                            withSize(contentSize.width, 1).
                                            build().
                                            apply {
                                                withFrontendString(
                                                        Format.SIDEBAR,
                                                        "Day ", Ticker.currentDay, ", ", Ticker.currentTimeString, " (", Ticker.currentTickObservable, ")")
                                            },
                                        Components
                                                .hbox()
                                                .withSize(fragmentWidth, 1)
                                                .withSpacing(BUTTON_ROW_SPACING)
                                                .build()
                                                .apply {
                                                    addComponents(
                                                            Components
                                                                    .button()
                                                                    .withText("${Symbols.DOUBLE_LINE_VERTICAL}")
                                                                    .withDecorations()
                                                                    .build()
                                                                    .apply {
                                                                        onActivated { Ticker.stop() }
                                                                    },
                                                            Components
                                                                    .button()
                                                                    .withText("${Symbols.TRIANGLE_RIGHT_POINTING_BLACK}")
                                                                    .withDecorations()
                                                                    .build()
                                                                    .apply {
                                                                        onActivated { Ticker.start() }
                                                                    },
                                                            Components
                                                                    .header()
                                                                    .withSize(fragmentWidth - 2 - (BUTTON_ROW_SPACING * 2), 1)
                                                                    .build()
                                                                    .apply {
                                                                        withFrontendString(Format.SIDEBAR,
                                                                                Ticker
                                                                                        .isPaused
                                                                                        .bindTransform { isPaused -> if(isPaused) { "PAUSED" } else { "" } })
                                                                    }
                                                    )
                                                }
                                )
                            }
}