package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components

/**
 * This fragment shows the list of markers on the selected tile.
 *
 * MARKERS ARE NOT YET IMPLEMENTED! This is just a dummy fragment for now!
 */
class MarkersFragment(observedTile: Property<Coordinate>) : InfoSidebarFragment(observedTile, title = "Markers", neededHeight = 1) {

    init {
        componentsContainer.addComponent(
                Components.listItem()
                        .withText("none")
                        .build()
        )
    }

}
