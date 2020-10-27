package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ticks.Ticker
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.Components

/**
 * Displays the current game time.
 */
class TickFragment(override val width: Int) : BaseFragment {
    companion object {
        const val FRAGMENT_HEIGHT = 2
    }

    override val root = Components.
                            vbox().
                            withSize(width, FRAGMENT_HEIGHT).
                            build().
                            apply {
                                addComponent(Components.header().withText("Current Tick"))
                                addComponent(Components.
                                    label().
                                    withSize(contentSize.width, 1).
                                    build().
                                    apply {
                                        textProperty.updateFrom(
                                                Ticker.currentTickObservable.bindTransform { it.toString() }
                                                        bindPlusWith createPropertyFrom(": ")
                                                        bindPlusWith Ticker.currentTimeString)
                                    })
                            }
}