package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Position

/**
 * The sidebar for the main user input. It contains the fragments used to select elements and send them commands.
 */
class InputSidebar(position: Position, height: Int) : VBox
    by Components.vbox()
        .withSpacing(2)
        .withSize(UiOptions.SIDEBAR_WIDTH, height)
        .withPosition(position)
        .withDecorations(ComponentDecorations.halfBlock())
        .build()