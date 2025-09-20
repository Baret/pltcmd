package de.gleex.pltcmd.game.application.editor

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import de.gleex.pltcmd.game.application.StartupHelper

fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired()) {
        return
    }
    Lwjgl3Application(PltcmdMapEditor(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("pltcmd")
        setWindowedMode(1800, 990)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}