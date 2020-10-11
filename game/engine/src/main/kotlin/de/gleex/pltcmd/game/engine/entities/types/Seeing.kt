package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.extensions.GameEntity

/**
 * This file contains code for entities that have the [VisibleAreaAttribute].
 */

/** Type marker for entities that are [Positionable] and can "see" (scan) their surroundings. */
interface Seeing : Positionable
typealias SeeingEntity = GameEntity<Seeing>
