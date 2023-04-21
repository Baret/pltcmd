package de.gleex.pltcmd.game.application.korge.mainmenu

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.EmptyScene
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.view.SContainer
import de.gleex.pltcmd.game.application.korge.intro.IntroScene
import kotlin.system.exitProcess

class MainMenuScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        uiVerticalStack(width = width * 0.5f, padding = 30.0) {
            uiButton("New game")
            uiButton("Load game")
            uiButton("Options") {
                onClick { sceneContainer.changeTo<IntroScene>() }
            }
            uiButton("Quit game") {
                onClick {
                    sceneContainer.changeTo<EmptyScene>()
                    exitProcess(0)
                }
            }
        }
    }
}
