package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment

class RadioSignalFragment(width: Int): Fragment {
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
                val strangthInput = Components.
                        textBox(width - strengthLabel.width).
                        build()

                addComponent(Components.
                        hbox().
                        withSpacing(1).
                        withSize(width, 1).
                        apply {
                            addComponent(strengthLabel)
                            addComponent(strangthInput)
                        }
                )
            }
}