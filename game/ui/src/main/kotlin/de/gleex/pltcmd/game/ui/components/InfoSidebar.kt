package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer

class InfoSidebar(height: Int) : Fragment {
    override val root =
            Components.panel()
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withDecorations(ComponentDecorations.halfBlock(ComponentDecorationRenderer.RenderingMode.INTERACTIVE))
                    .build()
}
