package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components

/**
 * Displays the observed coordinate.
 */
class CurrentCoordinateFragment(currentTile: ObservableValue<Coordinate>) :
        InfoSidebarFragment(
                currentTile,
                title = "Coordinate",
                neededHeight = 1
        ) {
    init {
        componentsContainer.addComponent(
                Components.label()
                        .withSize(SUB_COMPONENT_WIDTH, 1)
                        .build()
                        .apply {
                            withFrontendString(Format.SIDEBAR, currentInfoTile)
                        }
        )
    }
}
