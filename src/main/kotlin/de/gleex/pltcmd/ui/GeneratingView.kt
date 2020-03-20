package de.gleex.pltcmd.ui

import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapBlock
import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapGameArea
import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Header
import org.hexworks.zircon.api.component.ProgressBar
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/** Displays the progress of world generation. A miniature of the world is shown together with a progress bar. */
class GeneratingView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val progressBar = createProgressBar()
    private val header = createHeader()
    private val usedLines = progressBar.height + header.height
    val progressProperty = progressBar.progressProperty
    val incompleteWorld = IncompleteMapGameArea(Size.create(screen.width, screen.height - usedLines))

    init {
        progressProperty.onChange {
            if (it.newValue == progressBar.range.toDouble()) {
                println("progress bar is full! ! ! ! !")
                finished()
            }
        }
    }

    override fun onDock() {
        val mainPart = createMainPart()

        screen.addComponents(header, mainPart, progressBar)
    }

    private fun createHeader(): Header {
        return Components.header()
                .withText("Generating world...")
                .withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER)
                .build()
    }

    private fun createMainPart(): GameComponent<Tile, IncompleteMapBlock> {
        return GameComponents.newGameComponentBuilder<Tile, IncompleteMapBlock>()
                .withGameArea(incompleteWorld)
                .withSize(incompleteWorld.visibleSize.xLength, incompleteWorld.visibleSize.yLength - usedLines)
                .withPosition(0, header.height)
                .build()
    }

    private fun createProgressBar(): ProgressBar {
        return Components.progressBar()
                .withSize(screen.width, 1)
                .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_CENTER)
                .withNumberOfSteps(screen.width) // use 1 tile per step
                .withDisplayPercentValueOfProgress(true)
                .build()
    }

    private fun finished() {
        progressBar.isHidden = true
        header.text = "Generated world"
    }

}
