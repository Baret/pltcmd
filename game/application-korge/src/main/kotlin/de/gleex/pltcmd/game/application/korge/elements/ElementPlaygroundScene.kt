package de.gleex.pltcmd.game.application.korge.elements

import com.soywiz.korge.input.draggable
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import de.gleex.pltcmd.game.application.korge.elements.icons.IconCache
import de.gleex.pltcmd.game.application.korge.elements.icons.IconSelector
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.faction.Affiliation
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class ElementPlaygroundScene : Scene() {
    override suspend fun SContainer.sceneInit() {
//        backButton(sceneContainer)

        var posX = 150
        Affiliation
            .values()
            .forEach { affiliation ->
                val padding = 50
                val iconSelector = IconSelector(
                    affiliation = affiliation,
                    kind = ElementKind.Infantry,
                    tags = listOf("recon")
                )
                val image = image(IconCache.load(iconSelector)) {
                    position(posX, 200)
                    scaleWhileMaintainingAspect(ScalingOption.ByWidth(250.0))
                    draggable { }
                }
                image(IconCache.load(iconSelector)) {
                    scaleWhileMaintainingAspect(ScalingOption.ByWidth(100.0))
                    alignLeftToLeftOf(image, padding = padding)
                    alignTopToBottomOf(image, padding = padding)
                    draggable { }
                }
                posX += image.scaledWidth.toInt() + padding
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
