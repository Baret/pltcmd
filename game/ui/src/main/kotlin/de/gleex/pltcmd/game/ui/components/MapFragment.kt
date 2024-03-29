package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.renderers.MapCoordinateDecorationRenderer
import de.gleex.pltcmd.game.ui.renderers.MapGridDecorationRenderer
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode

/**
 * The main fragment of the game view. It displays the map with its decorations.
 */
class MapFragment(gameWorld: GameWorld) : Fragment {

    private val mapRenderer =
        GameComponents.newGameAreaComponentRenderer<Panel, Tile, GameBlock>(gameWorld, ProjectionMode.TOP_DOWN)

    override val root: Panel = Components.panel()
        .withPreferredSize(UiOptions.MAP_VIEW_WIDTH, UiOptions.MAP_VIEW_HEIGHT)
        .withDecorations(
            MapGridDecorationRenderer(),
            MapCoordinateDecorationRenderer(gameWorld)
        )
        .build()

    /**
     * This panel contains the game area rendering the [GameWorld].
     */
    val mapPanel: Panel = Components.panel()
        .withPreferredSize(root.contentSize)
        .withComponentRenderer(mapRenderer)
        .build()
        .apply {
            root.addComponent(this)
            tileset = UiOptions.MAP_TILESET
        }
}