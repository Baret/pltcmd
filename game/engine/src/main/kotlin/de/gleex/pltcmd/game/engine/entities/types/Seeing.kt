package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.extensions.*
import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldArea

/**
 * This file contains code for entities that have the [VisionAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
/**
 * An entity of type [Seeing]
 */
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visionAttribute: VisionAttribute
    get() = getAttribute(VisionAttribute::class)

internal var SeeingEntity.visionMutable: Vision
    get() = visionAttribute.vision
    set(value) {
        visionAttribute.vision = value
    }

/**
 * The [Vision] of this entity.
 */
// visible for the UI
val SeeingEntity.vision: Vision
    get() = visionMutable

internal val SeeingEntity.visibleTiles: WorldArea
    get() = vision.area

internal val SeeingEntity.visualRange: VisionPower
    get() = visionAttribute.visualRange

/**
 * Invokes the given suspend function when this entity is of type [Seeing].
 *
 * @return the result of [whenSeeing] when this entity is a [SeeingEntity]. False otherwise.
 */
suspend fun AnyGameEntity.invokeWhenSeeing(whenSeeing: suspend (SeeingEntity) -> Boolean): Boolean =
    castToSuspending<SeeingEntity, Seeing>(whenSeeing)
