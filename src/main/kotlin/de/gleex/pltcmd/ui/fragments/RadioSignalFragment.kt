package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components

class RadioSignalFragment(override val width: Int): BaseFragment {
    override val root = Components.
            vbox().
            withSize(width, 2).
            withSpacing(0).
            build().
            apply {
                val strengthLabel = Components.
                        label().
                        withText("Strength: ").
                        build()
                val strengthInput = Components.
                        textBox(width - strengthLabel.width).
                        build()

                addComponent(Components.
                        hbox().
                        withSpacing(1).
                        withSize(width, 1).
                        apply {
                            addComponent(strengthLabel)
                            addComponent(strengthInput)
                        }
                )
            }
}