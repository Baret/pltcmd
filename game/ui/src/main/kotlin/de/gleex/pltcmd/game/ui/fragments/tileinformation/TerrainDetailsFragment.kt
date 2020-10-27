package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Icon
import org.hexworks.zircon.api.component.Label

/**
 * Simply shows what terrain is present at that tile.
 */
class TerrainDetailsFragment(
        override val width: Int,
        currentTile: ObservableValue<Coordinate>,
        private val world: WorldMap
) : TileInformationFragment(currentTile) {

    companion object {
        private const val FILLER_TEXT = "Terrain "
    }

    private val terrainProperty: Property<Terrain> = Terrain.of(TerrainType.GRASSLAND, TerrainHeight.MIN).toProperty()

    private val icon: Icon = Components.icon()
            .withIcon(TileRepository.createTerrainTile(terrainProperty.value))
            .build()
            .apply { iconProperty.updateFrom(
                    terrainProperty.bindTransform { TileRepository.createTerrainTile(it) })
            }

    private val fixedText: Label = Components.label()
            .withText(FILLER_TEXT)
            .withSize(FILLER_TEXT.length, 1)
            .build()

    private val terrainDesciption: Label = Components.label()
            .withText(terrainProperty.value.toFrontendString(Format.SIDEBAR).value)
            .withSize(width - icon.size.width - fixedText.size.width, 1)
            .build()
            .apply {
                withFrontendString(Format.SIDEBAR, terrainProperty)
            }

    override val root =
            Components.hbox()
                    .withSize(width, 1)
                    .build()
                    .apply {
                        addComponents(
                                fixedText,
                                icon,
                                terrainDesciption
                        )
                        icon.tilesetProperty.updateValue(UiOptions.MAP_TILESET)
                    }

    override fun updateInformation(newCoordinate: Coordinate) {
        terrainProperty.updateValue(world[newCoordinate])
    }
}