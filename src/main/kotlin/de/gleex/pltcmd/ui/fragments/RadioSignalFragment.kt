package de.gleex.pltcmd.ui.fragments

import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.AbsoluteSignalLossAttenuation
import de.gleex.pltcmd.model.radio.AttenuationModel
import de.gleex.pltcmd.model.radio.PercentageReducingAttenuation
import de.gleex.pltcmd.model.radio.RadioSignal
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindToString
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.builder.Builder
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.fragment.MultiSelect
import kotlin.math.min

/**
 * A fragment used to configure the settings for the RadioSignalVisualizer.
 */
class RadioSignalFragment(override val width: Int) : BaseFragment {

    val selectedStrength: Property<Int> = createPropertyFrom(100)
    val selectedRange: Property<Int> = createPropertyFrom(5)

    override val root = Components.
            vbox().
            withSize(width, 9).
            withSpacing(0).
            build().
            apply {
                val header = buildHeader()
                val legend = buildLegend()

                val strengthLabel = buildLabelLine()
                val strengthInput = buildStrengthInputh(strengthLabel)

                val rangeLabel = buildLabelLine()
                val rangeInput = buildRangeInput(rangeLabel)

                val modelLabel = buildModelLabel()

                addComponents(
                        header,
                        legend,
                        strengthLabel,
                        strengthInput,
                        rangeLabel,
                        rangeInput,
                        modelLabel
                )

                val modelInput = buildModelInput()
                addFragment(modelInput)

                hiddenProperty.updateFrom(GameOptions.displayRadioSignals, true) {it.not()}
            }

    private fun buildHeader(): Header {
        return Components.
                header().
                withText("Radio signal settings").
                build()
    }

    private fun buildLegend(): HBox {
        return Components.
                hbox().
                withSpacing(0).
                withSize(width, 1).
                build().
                apply {
                    addComponents(*buildLegendText())
                }
    }

    private fun buildLegendText(): Array<Builder<Component>> {
        return arrayOf(
                Components.label().withText("100%"),
                Components.icon().withIcon(TileRepository.forSignal(1.0)),
                Components.label().withText(" ${RadioSignal.MIN_STRENGTH_THRESHOLD}%(min)"),
                Components.icon().withIcon(TileRepository.forSignal(RadioSignal.MIN_STRENGTH_THRESHOLD / 100.0)),
                Components.label().withText(" 0%"),
                Components.icon().withIcon(TileRepository.forSignal(0.0))
        )
    }

    private fun buildStrengthInputh(strengthLabel: Label): Slider {
        val strengthInput = Components.
                horizontalSlider().
                withMinValue(1).
                withMaxValue(1501).
                withNumberOfSteps(min(30, width)).
                withSize(width, 2).
                build().
                apply {
                    currentValue = selectedStrength.value
                    selectedStrength.updateFrom(currentValueProperty)
                }

        strengthLabel.textProperty.updateFrom(createPropertyFrom("Strength: ") bindPlusWith strengthInput.currentValueProperty.bindToString())
        return strengthInput
    }

    private fun buildLabelLine(text : String = ""): Label {
        return Components.
                label().
                withText(text).
                withSize(width, 1).
                build()
    }

    private fun buildRangeInput(rangeLabel: Label): Slider {
        val rangeInput = Components.
                horizontalSlider().
                withMinValue(1).
                withMaxValue(70).
                withNumberOfSteps(min(34, width)).
                build().
                apply {
                    currentValueProperty.value = selectedRange.value
                    selectedRange.updateFrom(currentValueProperty)
                }

        rangeLabel.textProperty.updateFrom(createPropertyFrom("Range: ") bindPlusWith rangeInput.currentValueProperty.bindToString())
        return rangeInput
    }

    private fun buildModelLabel(): Label = buildLabelLine("Attenuation model")

    private fun buildModelInput(): MultiSelect<Pair<String, AttenuationModel>> {
        val models = listOf(Pair("by percentage", PercentageReducingAttenuation()), Pair("absolute", AbsoluteSignalLossAttenuation()))

        return Fragments.
                multiSelect(width, models).
                withCallback { _, newValue -> GameOptions.attenuationModel.value = newValue.second }.
                withToStringMethod { it.first }.
                build()
    }

}