package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.signals.vision.VisualSignal

/**
 * This file contains code for entities that have the [VisionAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visionAttribute: VisionAttribute
    get() = getAttribute(VisionAttribute::class)

// visible for the UI
var SeeingEntity.vision: VisualSignal
    get() = visionAttribute.vision
    set(value) {
        visionAttribute.vision = value
    }

internal val SeeingEntity.lookingFrom
    get() = vision.origin

internal val SeeingEntity.visibleTiles
    get() = vision.area

internal val SeeingEntity.visualRange
    get() = visionAttribute.visualRange
