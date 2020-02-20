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
            withSize(width, 6).
            withSpacing(0).
            build().
            apply {

                val header = Components.
                        header().
                        withSize(this@RadioSignalFragment.width, 1).
                        withText("Radio signal settings").
                        build()

                val strengthLabel = Components.
                        label().
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
                strengthInput.currentValueProperty.value = 100

                val rangeLabel = Components.
                        label().
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
                rangeInput.currentValueProperty.value = 5


                addComponents(
                        header,
                        strengthLabel,
                        strengthInput,
                        rangeLabel,
                        rangeInput
                )
            }
}