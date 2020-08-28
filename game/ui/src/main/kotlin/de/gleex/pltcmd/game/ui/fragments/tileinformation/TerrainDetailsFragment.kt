package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
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
        world: WorldMap
) : TileInformationFragment(width, currentTile, world) {

    private val heightProperty: Property<TerrainHeight> = createPropertyFrom(TerrainHeight.MIN)
    private val typeProperty: Property<TerrainType> = createPropertyFrom(TerrainType.GRASSLAND)

    override val root =
            Components.label()
                    .withSize(width, 1)
                    .build()
                    .apply {
                        textProperty.updateFrom(
                                "Terrain: ".toProperty()
                                        .bindPlusWith(
                                                heightProperty.bindTransform { it.value.toString() }
                                                        .bindPlusWith(", ".toProperty())
                                                        .bindPlusWith(typeProperty.bindTransform { it.name })
                                        )
                        )
                    }

    override fun updateInformation(newCoordinate: Coordinate) {
        val terrainAt = world[newCoordinate]
        heightProperty.updateValue(terrainAt.height)
        typeProperty.updateValue(terrainAt.type)
    }
}