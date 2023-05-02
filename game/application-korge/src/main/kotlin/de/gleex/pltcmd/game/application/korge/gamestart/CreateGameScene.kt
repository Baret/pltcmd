package de.gleex.pltcmd.game.application.korge.gamestart

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import de.gleex.pltcmd.game.application.korge.common.backButton

class CreateGameScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        backButton(sceneContainer)
    }
}