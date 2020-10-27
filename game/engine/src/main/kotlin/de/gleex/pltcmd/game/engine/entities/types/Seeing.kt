package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.VisibleAreaAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute

/**
 * This file contains code for entities that have the [VisibleAreaAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
typealias SeeingEntity = GameEntity<Seeing>

private val SeeingEntity.visibleArea: VisibleAreaAttribute
    get() = getAttribute(VisibleAreaAttribute::class)


internal var SeeingEntity.lookingFrom
    get() = visibleArea.from
    set(value) {
        visibleArea.from = value
    }

internal var SeeingEntity.visibleTiles
    get() = visibleArea.tiles
    set(value) {
        visibleArea.tiles = value
    }
