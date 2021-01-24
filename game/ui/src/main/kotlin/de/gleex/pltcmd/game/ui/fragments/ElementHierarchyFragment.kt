package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.elements.blueprint.AbstractElementBlueprint
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory
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
            .withSize(fragmentWidth, fragmentHeight)
            .build()
            .apply {
                val elementNameHeader = Components
                    .header()
                    .withSize(contentSize.withHeight(1))
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
            .withSize(size)
            .withSpacing(0)
            .build()
        log.debug("Built hierarchy panel with size $size")
        subsFirstLevel
            .bindTransform { newSubs ->
                log.debug("Subs changed!")
                with(content) {
                    forEach { it.detach() }
                    clear()
                }
                newSubs
                    .forEach {
                        log.debug("Adding sub to vbox")
                        sub(1, it, size.withHeight(1))
                            .forEach { subLabel ->
                                content += vbox.addComponent(subLabel)
                            }
                    }
            }
        return vbox
    }

    private fun sub(level: Int, elementBlueprint: AbstractElementBlueprint<*>, size: Size): List<Label> {
        val hierarchySymbolsPrefix =
            "${Symbols.SINGLE_LINE_VERTICAL.toString().repeat(level - 1)}${Symbols.SINGLE_LINE_T_RIGHT} "
        val label = Components
            .label()
            .withText("w00t")
            .withSize(size)
            .build()
            .apply {
                log.debug("Built label with size $size and prefix $hierarchySymbolsPrefix")
                withFrontendString(format, hierarchySymbolsPrefix, elementBlueprint)
            }
        val labels: MutableList<Label> = mutableListOf(label)
        if (elementBlueprint is CommandingElementBlueprint) {
            elementBlueprint
                .subordinates
                .forEach { furtherSub ->
                    labels
                        .addAll(sub(level + 1, furtherSub, size))
                }
        }
        return labels
    }

    companion object {
        private val log = LoggerFactory.getLogger(ElementHierarchyFragment::class)
    }
}