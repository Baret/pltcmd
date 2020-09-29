package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components

/**
 * Displays the observed coordinate.
 */
class CurrentCoordinateFragment(override val width: Int, currentTile: ObservableValue<Coordinate>) : TileInformationFragment(currentTile) {

    private val currentCoordinate: Property<Coordinate> = Coordinate.zero.toProperty()

    override val root = Components.hbox().
            withSize(width, 1).
            build().
            apply {
                addComponent(Components.
                        label().
                        withSize(width, 1).
                        build().
                        apply {
                            withFrontendString(Format.SIDEBAR,
                                    "Coordinate: ", currentCoordinate)
                        })
            }

    override fun updateInformation(newCoordinate: Coordinate) {
        currentCoordinate.updateValue(newCoordinate)
    }
}
