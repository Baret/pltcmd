package de.gleex.pltcmd.ui

import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapBlock
import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapGameArea
import de.gleex.pltcmd.options.UiOptions
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Displays the progress of world generation. A miniature of the world is shown together with a progress bar. */
class GeneratingView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val footer = createFooter()
    private val progressBar = createProgressBar()
    private val header = createHeader()
    private val usedLines = progressBar.height + header.height
    private val scheduledUpdates = Executors.newSingleThreadScheduledExecutor()
    private var confirmCallback: () -> Unit = {}

    /** ratio from 0.0 to 1.0 */
    val progressProperty = createPropertyFrom(0.0)
    val incompleteWorld = IncompleteMapGameArea(Size.create(screen.width, screen.height - usedLines))

    init {
        footer.addComponent(progressBar)
        // update progress bar only every now and then because it lasts some time
        scheduledUpdates.scheduleAtFixedRate({
            progressBar.progress = progressBar.range * progressProperty.value
        }, 100L, 100L, TimeUnit.MILLISECONDS)
        progressProperty.onChange { if (it.newValue == 1.0) onFinished() }
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
        scheduledUpdates.shutdown()
        header.text = "Generated world"
        footer.clear()
        val continueButton = createContinueButton()
        footer.addComponent(continueButton)
    }

    private fun createContinueButton(): Button {
        val continueButton = Components.button()
                .withText("Click to continue")
                .withAlignmentWithin(footer, ComponentAlignment.BOTTOM_CENTER)
                .build()
        continueButton.onActivated {
            confirmCallback.invoke()
        }
        return continueButton
    }

    /** Will be called after the user confirmed the map generation. Replaces any previous callback. */
    fun onConfirmation(callback: () -> Unit) {
        confirmCallback = callback
    }

}
