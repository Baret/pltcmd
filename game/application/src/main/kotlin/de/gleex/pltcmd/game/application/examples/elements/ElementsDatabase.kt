package de.gleex.pltcmd.game.application.examples.elements

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.views.ElementsDatabase
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen

fun main() {
    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())

    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()
    screen.dock(ElementsDatabase(tileGrid))
}