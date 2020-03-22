package de.gleex.pltcmd.ui

import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.REXPaintResources
import org.hexworks.zircon.api.builder.animation.AnimationBuilder
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView
import org.hexworks.zircon.internal.animation.impl.DefaultAnimationFrame

/** Displays the title of the game. */
class TitleView(val tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    override fun onDock() {
        val frame = DefaultAnimationFrame(tileGrid.size, REXPaintResources.loadREXFile(ClassLoader.getSystemResourceAsStream("ingameLogo.xp")!!).toLayerList(), 1)
        val animation = AnimationBuilder.
            newBuilder().
            withLoopCount(1).
            withFps(1).
            addFrame(frame).
            build()
        screen.start(animation)
    }

    private fun addTitle(panel: Panel) {
        val title = Components.header()
                .withText("p l t c m d")
                .withAlignmentWithin(panel, ComponentAlignment.CENTER)
                .build()
        panel.addComponent(title)
    }

}