package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.extensions.onSelectionChanged
import org.hexworks.zircon.api.graphics.Layer

class LayersFragment(private val width: Int, private val layers: List<Layer>) : Fragment {
	override val root = Components.vbox().
			withSize(width, 2).
			build().apply {
					addComponent(Components.header().withText("Layers"))
					val checkBoxes = Components.hbox().
							withSize(width,1).
							withSpacing(2).
							build()
					layers.forEachIndexed { i, layer ->
						val checkBoxWidth = width / layers.size
						checkBoxes.addComponent(
								Components.checkBox().
										withText((i+1).toString()).
										build().
										apply {
											isSelected = true
											onSelectionChanged { layer.isHidden = layer.isHidden.not() }
										})
					}
					addComponent(checkBoxes)
				}

}