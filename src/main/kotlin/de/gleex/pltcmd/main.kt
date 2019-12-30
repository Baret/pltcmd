package de.gleex.pltcmd

import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import org.hexworks.zircon.api.SwingApplications


fun main() {
    SwingApplications.startApplication(UiOptions.buildAppConfig()).dock(GameView())
}