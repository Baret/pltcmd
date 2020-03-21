package de.gleex.pltcmd.ui

import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapBlock
import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapGameArea
import de.gleex.pltcmd.options.UiOptions
import kotlinx.coroutines.CompletableDeferred
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.component.Header
import org.hexworks.zircon.api.component.ProgressBar
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/**
 * Displays the progress of world generation. A miniature of the world is shown together with a progress bar.
 * The progress must be incremented externally!
 **/
class GeneratingView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val footer = createFooter()
    private val progressBar = createProgressBar()
    private val header = createHeader()
    private val usedLines = progressBar.height + header.height

    /** Value of the progress bar (0 to 100). Set to indicate generation progress. */
    val progressProperty = progressBar.progressProperty

    /** Will be set if the [progressProperty] filled the progress bar and the user clicks a button. */
    val finished: CompletableDeferred<Unit> = CompletableDeferred()

    /** World that is shown. Must be changed externally. */
    val incompleteWorld = IncompleteMapGameArea(Size.create(screen.width, screen.height - usedLines))

    init {
        footer.addComponent(progressBar)
        progressProperty.onChange {
            if (it.newValue == progressBar.range.toDouble()) {
                onFinished()
            }
        }
    }

    override fun onDock() {
        val mainPart = createMainPart()

        screen.addComponents(header, mainPart, footer)
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

    private fun createFooter(): HBox {
        return Components.hbox()
                .withSize(screen.width, 1)
                .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_CENTER)
                .build()
    }

    private fun createProgressBar(): ProgressBar {
        return Components.progressBar()
                .withAlignmentWithin(footer, ComponentAlignment.BOTTOM_CENTER)
                .withNumberOfSteps(footer.width) // use 1 tile per step
                .withDisplayPercentValueOfProgress(true)
                .build()
    }

    private fun onFinished() {
        header.text = "Generated world"
        footer.clear()
        val doneText = Components.button()
                .withText("Click to continue")
                .withAlignmentWithin(footer, ComponentAlignment.BOTTOM_CENTER)
                .build()
        footer.addComponent(doneText)
        doneText.onActivated {
            finished.complete(Unit)
        }
    }

}
