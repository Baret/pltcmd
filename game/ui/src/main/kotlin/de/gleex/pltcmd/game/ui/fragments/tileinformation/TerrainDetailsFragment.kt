package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components

/**
 * Simply shows what terrain is present at that tile.
 */
class TerrainDetailsFragment(
        currentTile: ObservableValue<Coordinate>,
        private val world: WorldMap
) : InfoSidebarFragment(currentTile, title = "Terrain", neededHeight = 1) {

    init {
        val terrainProperty: ObservableValue<Terrain> = currentInfoTile.bindTransform { world[it].terrain }
        componentsContainer.addComponent(
                Components.hbox()
                        .withSpacing(1)
                        .withPreferredSize(SUB_COMPONENT_WIDTH, 1)
                        .build()
                        .apply {
                            val icon = Components.icon()
                                    .withIcon(TileRepository.createTerrainTile(terrainProperty.value))
                                    .build()
                                    .apply {
                                        iconProperty.updateFrom(
                                                terrainProperty.bindTransform { TileRepository.createTerrainTile(it) })
                                        tilesetProperty.updateValue(UiOptions.MAP_TILESET)
                                    }
                            addComponents(
                                    icon,
                                    Components.label()
                                            .withPreferredSize(SUB_COMPONENT_WIDTH - icon.size.width - 1, 1)
                                            .build()
                                            .apply {
                                                withFrontendString(Format.SIDEBAR, terrainProperty)
                                            }
                            )
                        }
        )
    }


}