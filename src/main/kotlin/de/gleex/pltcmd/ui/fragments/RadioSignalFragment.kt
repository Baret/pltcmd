package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components

class RadioSignalFragment(override val width: Int): BaseFragment {
    override val root = Components.
            vbox().
            withSize(width, 5).
            withSpacing(0).
            build().
            apply {
                val strengthLabel = Components.
                        label().
                        withSize(this@RadioSignalFragment.width, 1).
                        withText("Strength: ").
                        build()
                val strengthInput = Components.
                        horizontalSlider().
                        //withInitialValue(15).
                        withMinValue(1).
                        withMaxValue(1000).
                        withSize(this@RadioSignalFragment.width, 2).
                        build()

                addComponent(strengthLabel)
                addComponent(strengthInput)

//                addComponent(Components.
//                        vbox().
//                        withSpacing(0).
//                        withSize(width, 3).
//                        apply {
//                            addComponent(strengthLabel)
//                            addComponent(strengthInput)
//                        }
//                )
            }
}