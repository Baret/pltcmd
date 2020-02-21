package de.gleex.pltcmd.ui.fragments

import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.AbsoluteSignalLossAttenuation
import de.gleex.pltcmd.model.radio.PercentageReducingAttenuation
import de.gleex.pltcmd.model.radio.RadioSignal
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindToString
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import kotlin.math.min

/**
 * A fragment used to configure the settings for the RadioSignalVisualizer.
 */
class RadioSignalFragment(override val width: Int): BaseFragment {

    val selectedStrength: Property<Int> = createPropertyFrom(100)
    val selectedRange: Property<Int> = createPropertyFrom(5)

    override val root = Components.
            vbox().
            withSize(width, 9).
            withSpacing(0).
            build().
            apply {

                val header = Components.
                        header().
                        withSize(this@RadioSignalFragment.width, 1).
                        withText("Radio signal settings").
                        build()

                val legend = Components.
                        hbox().
                        withSpacing(0).
                        withSize(this@RadioSignalFragment.width, 1).
                        build().
                        apply {
                            addComponents(
                                    Components.label().withText("100%"),
                                    Components.icon().withIcon(TileRepository.forSignal(1.0)),
                                    Components.label().withText(" ${RadioSignal.MIN_STRENGTH_THRESHOLD}%(min)"),
                                    Components.icon().withIcon(TileRepository.forSignal(RadioSignal.MIN_STRENGTH_THRESHOLD / 100.0)),
                                    Components.label().withText(" 0%"),
                                    Components.icon().withIcon(TileRepository.forSignal(0.0))
                            )
                        }

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

                val modelLabel = Components.
                        label().
                        withText("Attenuation model").
                        build()

                val models = listOf(Pair("by percentage", PercentageReducingAttenuation()), Pair("absolute", AbsoluteSignalLossAttenuation()))

                val modelInput = Fragments.
                        multiSelect(this@RadioSignalFragment.width, models).
                        withCallback { _, newValue -> GameOptions.attenuationModel.value = newValue.second }.
                        withToStringMethod { it.first }.
                        build()

                addComponents(
                        header,
                        legend,
                        strengthLabel,
                        strengthInput,
                        rangeLabel,
                        rangeInput,
                        modelLabel
                )
                addFragment(modelInput)
            }
}