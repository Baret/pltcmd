package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.radio.broadcasting.*
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindToString
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.builder.Builder
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.fragment.Selector
import kotlin.math.min

/**
 * A fragment used to configure the settings for the RadioSignalVisualizer.
 */
class RadioSignalFragment(override val width: Int) : BaseFragment {

    val selectedStrength: Property<Int> = createPropertyFrom(200)
    val selectedRange: Property<Int> = createPropertyFrom(20)

    override val root = Components.
            vbox().
            withSize(width, 9).
            withSpacing(0).
            build().
            apply {
                val header = buildHeader()
                val legend = buildLegend()

                val powerLabel = buildLabelLine()
                val powerInput = buildPowerInput(powerLabel)

                val rangeLabel = buildLabelLine()
                val rangeInput = buildRangeInput(rangeLabel)

                val modelLabel = buildModelLabel()

                addComponents(
                        header,
                        legend,
                        powerLabel,
                        powerInput,
                        rangeLabel,
                        rangeInput,
                        modelLabel
                )

                val modelInput = buildModelInput()
                addFragment(modelInput)

                hiddenProperty.updateFrom(GameOptions.displayRadioSignals, true, Boolean::not)
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
        val minPower = RadioSignal.MIN_POWER_THRESHOLD
        return arrayOf(
                Components.label().withText("100%"),
                Components.icon().withIcon(TileRepository.forSignal(SignalStrength.FULL)),
                Components.label().withText(" ${minPower.toInt()}%(min)"),
                Components.icon().withIcon(TileRepository.forSignal(minPower.toSignalStrength())),
                Components.label().withText(" 0%"),
                Components.icon().withIcon(TileRepository.forSignal(SignalStrength.NONE))
        )
    }

    private fun buildPowerInput(powerLabel: Label): Slider {
        val strengthInput = Components.
                horizontalSlider().
                withMinValue(1).
                withMaxValue(5001).
                withNumberOfSteps(min(30, width)).
                withSize(width, 2).
                build().
                apply {
                    currentValue = selectedStrength.value
                    selectedStrength.updateFrom(currentValueProperty)
                }

        powerLabel.textProperty.updateFrom(
                createPropertyFrom("Power: ") bindPlusWith strengthInput.currentValueProperty.bindToString())
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

        rangeLabel.textProperty.updateFrom(
                createPropertyFrom("Range: ") bindPlusWith rangeInput.currentValueProperty.bindToString())
        return rangeInput
    }

    private fun buildModelLabel(): Label = buildLabelLine("Attenuation model")

    private fun buildModelInput(): Selector<Pair<String, AttenuationModel>> {
        val models = listOf(
                Pair("by percentage", PercentageReducingAttenuation()),
                Pair("absolute", AbsoluteSignalLossAttenuation())
        )

        return Fragments.
                selector(width, models).
                withToStringMethod(Pair<String, AttenuationModel>::first).
                build()
                .apply {
                    selectedValue.onChange { AttenuationModel.DEFAULT.value = it.newValue.second }
                }
    }

}