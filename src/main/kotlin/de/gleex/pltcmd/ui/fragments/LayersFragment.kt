package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.extensions.onDisabledChanged
import org.hexworks.zircon.api.graphics.Layer

class LayersFragment(override val width: Int, private val layers: List<Layer>) : BaseFragment {
    override val root = Components.vbox().
            withSize(width, 2).
            build()
            .apply {
                addComponent(Components.header().withText("Layers"))
                val checkBoxes = Components.hbox().
                        withSize(width, 1).
                        withSpacing(2).
                        build()
                layers.forEachIndexed { i, layer ->
                    checkBoxes.addComponent(
                            Components.checkBox().
                                withText((i + 1).toString()).
                                build().
                                apply {
                                    isSelected = true
                                    onDisabledChanged() { event -> layer.isHidden = event.newValue.not() }
                                }
                            )
                }
                addComponent(checkBoxes)
            }

}