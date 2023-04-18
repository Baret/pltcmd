package de.gleex.pltcmd.game.application.korge.intro

import com.soywiz.klock.seconds
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.delay
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korge.view.text
import de.gleex.pltcmd.game.application.korge.mainmenu.MainMenuScene

class IntroScene: Scene() {
    override suspend fun SContainer.sceneInit() {
        text("pltcmd", textSize = Text.DEFAULT_TEXT_SIZE * 3)

        text("welcome, commander")

        delay(5.seconds)
        sceneContainer.changeTo<MainMenuScene>(transition = MaskTransition(TransitionFilter.Transition.SWEEP), time = 2.seconds)
    }
}