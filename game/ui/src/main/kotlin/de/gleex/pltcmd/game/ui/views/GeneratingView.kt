package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.mapgeneration.IncompleteMapBlock
import de.gleex.pltcmd.game.ui.mapgeneration.IncompleteMapGameArea
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.ComponentEventType
import org.hexworks.zircon.api.view.base.BaseView

/**
 * An incomplete map is shown together with a progress bar. Both must be altered externally. After calling
 * [showFinished] a button will become visible that triggers a callback when used.
 * @see onConfirmation to set the callback for the button.
 **/
class GeneratingView(tileGrid: TileGrid, worldSizeInTiles: Size) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val footer = createFooter()
    private val footerHandle: AttachedComponent
    private val progressBar = createProgressBar()
    private val projectionMode = ProjectionMode.TOP_DOWN.toProperty()
    private val header = createHeader()
    private val usedLines = progressBar.height + header.height
    private var confirmCallback: () -> Unit = {}

    val incompleteWorld = IncompleteMapGameArea(Size.create(screen.width, screen.height - usedLines), worldSizeInTiles)

    init {
        val mainPart = createMainPart()

        footerHandle = footer.addComponent(progressBar)

        screen.addComponents(createHeaderPanel(), mainPart, footer)
        mainPart.tilesetProperty.updateValue(UiOptions.MAP_TILESET)
    }

    fun setProgressBarTo(progress: Double) {
        progressBar.progress = progressBar.range * progress
    }

    private fun createHeader(): Header {
        return Components.header()
                .withText("Generating world...")
                .build()
    }

    private fun createHeaderPanel(): HBox {
        val toggle = Components.toggleButton()
                .withIsSelected(false)
                .withText("3D")
                .build()
        toggle.processComponentEvents(ComponentEventType.ACTIVATED) {
            val newProjectionMode = if(projectionMode.value == ProjectionMode.TOP_DOWN) {
                ProjectionMode.TOP_DOWN_OBLIQUE_FRONT
            } else {
                ProjectionMode.TOP_DOWN
            }
            projectionMode.updateValue(newProjectionMode)
        }
        val spacing = (screen.size.width - toggle.size.width - header.size.width) / 2
        val hBox = Components
                .hbox()
                .withSpacing(spacing)
                .withSize(screen.size.width, 1)
                .build()
        hBox.addComponents(toggle, header)
        return hBox
    }

    private fun createMainPart(): Panel {
        val renderer = GameComponents.newGameAreaComponentRenderer<Panel, Tile, IncompleteMapBlock>(incompleteWorld,
                projectionMode = projectionMode
        )
        return Components.panel()
                .withSize(screen.width, screen.height - usedLines)
                .withPosition(0, header.height)
                .withComponentRenderer(renderer)
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
                .withRange(footer.width)
                .withNumberOfSteps(footer.width) // use 1 tile per step
                .withDisplayPercentValueOfProgress(true)
                .build()
    }

    /** Changes the header and displays a button to the user to click to continue. */
    fun showFinished() {
        header.text = "Generated world"
        footerHandle.detach()
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

    override fun onUndock() {
        // TODO after updating Zircon version which fixed https://github.com/Hexworks/zircon/issues/376
        // incompleteWorld.dispose()
        super.onUndock()
    }
}
