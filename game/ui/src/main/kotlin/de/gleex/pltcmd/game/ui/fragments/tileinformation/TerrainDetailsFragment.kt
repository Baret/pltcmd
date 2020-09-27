package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components

/**
 * Simply shows what terrain is present at that tile.
 */
class TerrainDetailsFragment(
        width: Int,
        currentTile: ObservableValue<Coordinate>,
        private val world: WorldMap
) : TileInformationFragment(width, currentTile) {

    private val terrainProperty: Property<Terrain> = Terrain.of(TerrainType.GRASSLAND, TerrainHeight.MIN).toProperty()

    override val root =
            Components.label()
                    .withSize(width, 1)
                    .build()
                    .apply {
                        withFrontendString(Format.SIDEBAR, "Terrain: ", terrainProperty)
                    }

    override fun updateInformation(newCoordinate: Coordinate) {
        terrainProperty.updateValue(world[newCoordinate])
    }
}