package de.gleex.pltcmd.game.application.korge.elements

import com.soywiz.korge.input.draggable
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import de.gleex.pltcmd.game.application.korge.elements.icons.IconCache
import de.gleex.pltcmd.game.application.korge.elements.icons.IconSelector
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

class ElementPlaygroundScene: Scene() {
    override suspend fun SContainer.sceneInit() {
//        backButton(sceneContainer)

        val iconSelector = IconSelector()
        image(IconCache.load(iconSelector)) {
                centerOnStage()
                scaleWhileMaintainingAspect(ScalingOption.ByWidth(160.0))
                draggable {  }
                log.info { "There should be an image: $this" }
                log.info { "width=$scaledWidth height=$scaledHeight pos=$pos" }
            }


//            vectorImage(observableSvg.currentValue, autoScaling = true) {
//                scaleWhileMaintainingAspect(ScalingOption.ByWidth(250.0))
//                centerOnStage()
//                log.info { "Created vector image. width=$width height=$height pos=$pos" }
//                log.info { "shape: $shape" }
//            }

//        uiHorizontalStack(height = sceneHeight.toDouble()) {
//            uiVerticalStack {
//                val affiliationGroup = UIRadioButtonGroup()
//                uiRadioButton(text = "Friendly", group = affiliationGroup, checked = true)
//                uiRadioButton(text = "Hostile", group = affiliationGroup)
//                uiRadioButton(text = "Neutral", group = affiliationGroup)
//                uiRadioButton(text = "Unknown", group = affiliationGroup)
//            }
//            // TODO: displaying an SVG in a UI does not yet work :\
//            //uiImage(bitmap = IconCache.load(IconSelector()).render())
//        }
    }
}
