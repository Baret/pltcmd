package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Component

class ElementHierarchyFragment(
    private val superOrdinate: ObservableValue<CommandingElementBlueprint>,
    override val fragmentWidth: Int,
    val fragmentHeight: Int
): BaseFragment {

    private val format: Format = Format.forWidth(fragmentWidth)

    override val root: Component =
        Components
            .hbox()
            .withSize(fragmentWidth, fragmentHeight)
            .build()

    init {
        superOrdinate.onChange { showHierarchy(it.newValue) }

    }

    private fun showHierarchy(superOrdinate: CommandingElementBlueprint) {
        //clear()

    }
}