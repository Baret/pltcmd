package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.cobalt.datatypes.Maybe

/**
 * The entity type for Forward Operating Bases (FOBs).
 */
object FOBType : BaseEntityType("FOB", "A stationary forward operating base (FOB)."), Communicating, Factionable,
    Seeing, Remembering

/**
 * An entity of type [FOBType].
 */
typealias FOBEntity = GameEntity<FOBType>

/**
 * Invokes [whenFOB] if this entity is an [FOBEntity]. When the type is not [FOBType],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenFOB]
 */
fun <R> AnyGameEntity.asFOBEntity(whenFOB: (FOBEntity) -> R): Maybe<R> =
    tryCastTo<FOBEntity, FOBType, R>(whenFOB)
