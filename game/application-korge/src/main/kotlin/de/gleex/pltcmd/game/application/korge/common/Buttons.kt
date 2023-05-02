package de.gleex.pltcmd.game.application.korge.common

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.alignXY

/**
 * Adds a button that navigates back in the given sceneContainer.
 */
fun SContainer.backButton(sceneContainer: SceneContainer) = uiButton("Back") {
    onClick { sceneContainer.back() }
    alignXY(other = this@backButton,
        ratio = 0.95,
        inside = true,
        doX = false,
        padding = 25.0)
}