package de.gleex.pltcmd.game.application.korge.mainmenu

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.korui.korui
import com.soywiz.korge.view.SContainer
import com.soywiz.korui.button
import com.soywiz.korui.label
import com.soywiz.korui.layout.vertical

class MainMenuScene: Scene() {
    override suspend fun SContainer.sceneInit() {
        korui {
            vertical {
                label("New game")
                label("Load game")
                label("Options")
                button("Quit game")
            }
        }
    }
}
