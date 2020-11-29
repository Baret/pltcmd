package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldArea

/**
 * This file contains code for entities that have the [VisionAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visionAttribute: VisionAttribute
    get() = getAttribute(VisionAttribute::class)

// visible for the UI
var SeeingEntity.vision: Vision
    get() = visionAttribute.vision
    set(value) {
        visionAttribute.vision = value
    }

internal val SeeingEntity.visibleTiles: WorldArea
    get() = vision.area

internal val SeeingEntity.visualRange: VisionPower
    get() = visionAttribute.visualRange
