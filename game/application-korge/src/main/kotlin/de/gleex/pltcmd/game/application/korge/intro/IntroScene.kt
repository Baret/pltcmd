package de.gleex.pltcmd.game.application.korge.intro

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import de.gleex.pltcmd.game.application.korge.mainmenu.MainMenuScene

class IntroScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        val text1 = text("pltcmd", textSize = Text.DEFAULT_TEXT_SIZE * 3) {
            centerOnStage()
        }
        text("welcome, commander") {
                alignBottomToTopOf(text1)
                centerXOnStage()
            }

        text("click to continue") {
            centerXOnStage()
            alignBottomToBottomOf(this@sceneInit)
        }

        onClick { sceneContainer.pushTo<MainMenuScene>() }
    }
}
