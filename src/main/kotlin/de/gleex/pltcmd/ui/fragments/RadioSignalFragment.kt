package de.gleex.pltcmd.ui.fragments

import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindToString
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components
import kotlin.math.min

/**
 * A fragment used to configure the settings for the RadioSignalVisualizer.
 */
class RadioSignalFragment(override val width: Int): BaseFragment {

    val selectedStrength: Property<Int> = createPropertyFrom(100)
    val selectedRange: Property<Int> = createPropertyFrom(5)

    override val root = Components.
            vbox().
            withSize(width, 7).
            withSpacing(0).
            build().
            apply {
                val strengthLabel = Components.
                        header().
                        withSize(this@RadioSignalFragment.width, 1).
                        build()
                val strengthInput = Components.
                        horizontalSlider().
                        withMinValue(1).
                        withMaxValue(1501).
                        withNumberOfSteps(min(30, this@RadioSignalFragment.width)).
                        withSize(this@RadioSignalFragment.width, 2).
                        build()

                strengthLabel.textProperty.updateFrom(createPropertyFrom("Strength: ") bindPlusWith strengthInput.currentValueProperty.bindToString())
                selectedStrength.updateFrom(strengthInput.currentValueProperty)

                val rangeLabel = Components.
                        header().
                        withSize(this@RadioSignalFragment.width, 1).
                        build()
                val rangeInput = Components.
                        horizontalSlider().
                        withMinValue(1).
                        withMaxValue(70).
                        withNumberOfSteps(min(34, this@RadioSignalFragment.width)).
                        build()

                rangeLabel.textProperty.updateFrom(createPropertyFrom("Range: ") bindPlusWith rangeInput.currentValueProperty.bindToString())
                selectedRange.updateFrom(rangeInput.currentValueProperty)


                addComponents(strengthLabel, strengthInput, rangeLabel, rangeInput)
            }
}