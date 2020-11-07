package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.Components

/**
 * This fragment shows the list of markers on the selected tile.
 *
 * MARKERS ARE NOT YET IMPLEMENTED! This is just a dummy fragment for now!
 */
class MarkersFragment(override val width: Int, observedTile: Property<Coordinate>) : TileInformationFragment(observedTile) {

    override val root = Components.vbox()
            .withSize(width, 2)
            .withSpacing(0)
            .build()
            .apply {
                addComponents(
                        Components.header()
                                .withText("Markers:"),
                        Components.listItem()
                                .withText("none")
                )
            }

}
