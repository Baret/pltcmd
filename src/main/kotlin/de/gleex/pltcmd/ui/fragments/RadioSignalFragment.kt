package de.gleex.pltcmd.ui.fragments

import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindToString
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components
import kotlin.math.min

class RadioSignalFragment(override val width: Int): BaseFragment {

    val selectedStrength: Property<Int> = createPropertyFrom(100)

    override val root = Components.
            vbox().
            withSize(width, 5).
            withSpacing(0).
            build().
            apply {
                val strengthLabel = Components.
                        label().
                        withSize(this@RadioSignalFragment.width, 1).
                        build()
                val strengthInput = Components.
                        horizontalSlider().
                        withMinValue(1).
                        withMaxValue(1500).
                        withNumberOfSteps(min(30, this@RadioSignalFragment.width)).
                        withSize(this@RadioSignalFragment.width, 2).
                        build()

                strengthLabel.textProperty.updateFrom(createPropertyFrom("Strength: ") bindPlusWith strengthInput.currentValueProperty.bindToString())

                selectedStrength.updateFrom(strengthInput.currentValueProperty)

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