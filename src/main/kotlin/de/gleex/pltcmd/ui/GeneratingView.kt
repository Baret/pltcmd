package de.gleex.pltcmd.ui

import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapBlock
import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapGameArea
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.renderers.MapGridDecorationRenderer
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.ProgressBar
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/** Displays the progress of world generation. A miniature of the world is shown together with a progress bar. */
class GeneratingView(private val gameWorld: IncompleteMapGameArea, tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val progressBar = createProgressBar()
    val progressProperty = progressBar.progressProperty

    override fun onDock() {
        val header = createHeader()
        val mainPart = createMainPart()

        screen.addComponents(header, mainPart, progressBar)
    }

    private fun createHeader(): Label {
        return Components.label()
                .withText("Generating world...")
                .withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER)
                .build()
    }

    private fun createMainPart(): Panel {
        val mainPart = Components.panel()
                .withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT)
                .withAlignmentWithin(screen, ComponentAlignment.CENTER)
                .withDecorations(MapGridDecorationRenderer())
                .build()

        val map = GameComponents.newGameComponentBuilder<Tile, IncompleteMapBlock>()
                .withGameArea(gameWorld)
                .withSize(gameWorld.visibleSize.to2DSize())
                .withAlignmentWithin(mainPart, ComponentAlignment.CENTER)
                .build()
        mainPart.addComponent(map)
        return mainPart
    }

    private fun createProgressBar(): ProgressBar {
        return Components.progressBar()
                .withSize(UiOptions.MAP_VIEW_WDTH, 1)
                .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_CENTER)
                .withDisplayPercentValueOfProgress(true)
                .build()
    }

}
