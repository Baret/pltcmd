package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.extensions.GameEntity
import org.hexworks.amethyst.api.base.BaseEntityType

/**
 * The entity type for Forward Operating Bases (FOBs).
 */
object FOBType : BaseEntityType("FOB", "A stationary forward operating base (FOB)."), Communicating, Seeing

/**
 * An entity of type [FOBType].
 */
typealias FOBEntity = GameEntity<FOBType>
