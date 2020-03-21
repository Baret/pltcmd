package de.gleex.pltcmd.ui

import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapBlock
import de.gleex.pltcmd.model.mapgenerators.ui.IncompleteMapGameArea
import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

/**
 * An incomplete map is shown together with a progress bar. Both must be altered externally. After calling
 * [showFinished] a button will shown that triggers a callback when used.
 **/
class GeneratingView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val footer = createFooter()
    private val progressBar = createProgressBar()
    private val header = createHeader()
    private val usedLines = progressBar.height + header.height
    private var confirmCallback: () -> Unit = {}

    val incompleteWorld = IncompleteMapGameArea(Size.create(screen.width, screen.height - usedLines))

    init {
        val mainPart = createMainPart()
        footer.addComponent(progressBar)

        screen.addComponents(header, mainPart, footer)
    }

    fun setProgressBarTo(progress: Double) {
        progressBar.progress = progressBar.range * progress
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
                .withSize(screen.width, screen.height - usedLines)
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

    /** Changes the header and displays a button to the user to click to continue. */
    fun showFinished() {
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
