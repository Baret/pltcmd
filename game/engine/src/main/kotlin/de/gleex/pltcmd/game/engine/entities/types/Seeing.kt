package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.VisibleAreaAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea

/**
 * This file contains code for entities that have the [VisibleAreaAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visibleArea: VisibleAreaAttribute
    get() = getAttribute(VisibleAreaAttribute::class)

internal fun SeeingEntity.updateVision(lookingFrom: Coordinate, visibleTiles: CoordinateArea) {
    visibleArea.from = lookingFrom
    visibleArea.area = visibleTiles
}

internal val SeeingEntity.lookingFrom
    get() = visibleArea.from

internal val SeeingEntity.visibleTiles
    get() = visibleArea.area
