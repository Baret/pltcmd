package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import de.gleex.pltcmd.model.elements.units.Units
import org.hexworks.cobalt.databinding.api.binding.Binding
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.AttachedComponent
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Size

/**
 * Shows a summary of the units contained in [superOrdinate].
 */
class ElementUnitsFragment(
    private val superOrdinate: ObservableValue<CommandingElementBlueprint>,
    override val fragmentWidth: Int,
    val fragmentHeight: Int
) : BaseFragment {

    private val format: Format = Format.forWidth(fragmentWidth)

    private val elementName: FrontendString<CommandingElementBlueprint> =
        superOrdinate.toFrontendString(format)

    private val totalUnits: FrontendString<String> =
        superOrdinate.bindTransform { "Total units: ${it.new().totalUnits}" }.toFrontendString(format)

    private val unitSummary: Binding<Map<Units, Int>> =
        superOrdinate.bindTransform { superOrdinate ->
            superOrdinate
                .new()
                .allUnits
                .groupBy { unit -> unit.blueprint }
                .mapValues { it.value.size }
        }

    private val summaryItems: MutableList<AttachedComponent> = mutableListOf()

    override val root: Component =
        Components
            .vbox()
            .withSize(fragmentWidth, fragmentHeight)
            .build()
            .apply {
                val elementNameHeader = Components
                    .header()
                    .withSize(contentSize.withHeight(1))
                    .build()
                    .apply {
                        withFrontendString(elementName)
                    }
                val totalUnitsLabel = Components
                    .label()
                    .withSize(contentSize.withHeight(2))
                    .build()
                    .apply {
                        withFrontendString(totalUnits)
                    }
                addComponents(
                    elementNameHeader,
                    totalUnitsLabel,
                    unitSummaryPanel(contentSize.withRelativeHeight(-(elementNameHeader.height + totalUnitsLabel.height)))
                )
            }

    private fun unitSummaryPanel(size: Size): VBox {
        val vbox = Components
            .vbox()
            .withSize(size)
            .build()
        unitSummary
            .bindTransform { summaryMap ->
                with(summaryItems) {
                    forEach { it.detach() }
                    clear()
                }
                summaryMap
                    .forEach { (blueprint, count) ->
                        summaryItems.add(
                            vbox.addComponent(
                                Components
                                    .label()
                                    .withSize(vbox.contentSize.withHeight(1))
                                    .build()
                                    .apply {
                                        withFrontendString(format, "${count.toString().padStart(3)}x ", blueprint)
                                    }
                            )
                        )
                    }
            }
        return vbox
    }
}