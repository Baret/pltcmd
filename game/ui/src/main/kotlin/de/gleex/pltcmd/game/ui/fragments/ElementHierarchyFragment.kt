package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.elements.blueprint.AbstractElementBlueprint
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.AttachedComponent
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Symbols

class ElementHierarchyFragment(
    private val superOrdinate: ObservableValue<CommandingElementBlueprint>,
    override val fragmentWidth: Int,
    val fragmentHeight: Int
) : BaseFragment {
    private val format: Format = Format.forWidth(fragmentWidth)

    private val subsFirstLevel = superOrdinate.bindTransform { it.subordinates }

    private val content: MutableList<AttachedComponent> = mutableListOf()

    override val root: Component =
        Components
            .vbox()
            .withPreferredSize(fragmentWidth, fragmentHeight)
            .build()
            .apply {
                val elementNameHeader = Components
                    .header()
                    .withPreferredSize(contentSize.withHeight(1))
                    .build()
                    .apply {
                        withFrontendString(superOrdinate.toFrontendString(format))
                    }
                addComponents(
                    elementNameHeader,
                    hierarchyPanel(contentSize.withRelativeHeight(-elementNameHeader.height))
                )
            }

    private fun hierarchyPanel(size: Size): VBox {
        val vbox = Components
            .vbox()
            .withPreferredSize(size)
            .withSpacing(0)
            .build()
        subsFirstLevel
            .bindTransform { newSubs ->
                with(content) {
                    forEach { it.detach() }
                    clear()
                }
                newSubs
                    .forEachIndexed { index, blueprint ->
                        sub(1, blueprint, size.withHeight(1), index == newSubs.lastIndex, "")
                            .forEach { subLabel ->
                                if (content.size < size.height) {
                                    content += vbox.addComponent(subLabel)
                                }
                            }
                    }
            }
        return vbox
    }

    private fun sub(
        level: Int,
        elementBlueprint: AbstractElementBlueprint<*>,
        size: Size,
        isLast: Boolean,
        parentPrefix: String
    ): List<Label> {
        val hierarchySymbolsPrefix = hierarchySymbols(level, isLast, parentPrefix)
        val label = Components
            .label()
            .withText("w00t")
            .withPreferredSize(size)
            .build()
            .apply {
                withFrontendString(format, hierarchySymbolsPrefix, elementBlueprint)
            }
        val labels: MutableList<Label> = mutableListOf(label)
        if (elementBlueprint is CommandingElementBlueprint) {
            elementBlueprint
                .subordinates
                .forEachIndexed { index, furtherSub ->
                    labels
                        .addAll(
                            sub(
                                level + 1,
                                furtherSub,
                                size,
                                index == elementBlueprint.subordinates.lastIndex,
                                hierarchySymbolsPrefix
                            )
                        )
                }
        }
        return labels
    }

    private fun hierarchySymbols(level: Int, isLast: Boolean, parentPrefix: String): String {
        val treeSymbol = if (isLast) {
            Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER
        } else {
            Symbols.SINGLE_LINE_T_RIGHT
        }
        val prefix = parentPrefix
            .map {
                if (it == Symbols.SINGLE_LINE_T_RIGHT || it == Symbols.SINGLE_LINE_VERTICAL) {
                    Symbols.SINGLE_LINE_VERTICAL
                } else {
                    " "
                }
            }
            .joinToString("")
        return "$prefix$treeSymbol"
    }
}