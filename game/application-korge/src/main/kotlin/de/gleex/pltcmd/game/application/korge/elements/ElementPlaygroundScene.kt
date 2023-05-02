package de.gleex.pltcmd.game.application.korge.elements

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UIRadioButtonGroup
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.ui.uiRadioButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.view.SContainer
import de.gleex.pltcmd.game.application.korge.common.backButton

class ElementPlaygroundScene: Scene() {
    override suspend fun SContainer.sceneInit() {
        backButton(sceneContainer)
        uiHorizontalStack(height = sceneHeight.toDouble()) {
            uiVerticalStack {
                val affiliationGroup = UIRadioButtonGroup()
                uiRadioButton(text = "Friendly", group = affiliationGroup, checked = true)
                uiRadioButton(text = "Hostile", group = affiliationGroup)
                uiRadioButton(text = "Neutral", group = affiliationGroup)
                uiRadioButton(text = "Unknown", group = affiliationGroup)
            }
            // TODO: displaying an SVG in a UI does not yet work :\
            //uiImage(bitmap = IconCache.load(IconSelector()).render())
        }
    }
}