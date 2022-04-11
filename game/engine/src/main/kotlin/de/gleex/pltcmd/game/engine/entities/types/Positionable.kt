package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This file contains code for entities that have the [PositionAttribute].
 */

/** Type marker for entities that have the PositionAttribute */
interface Positionable : EntityType
typealias PositionableEntity = GameEntity<Positionable>

/** Access to the [Property] of the [PositionAttribute] of a [PositionableEntity] */
var PositionableEntity.position: ObservableValue<Coordinate>
    get() = getAttribute(PositionAttribute::class).coordinate
    internal set(value) {
        getAttribute(PositionAttribute::class).coordinate.updateFrom(value)
    }

/** Access to the value of the [PositionAttribute] of a [PositionableEntity] */
var PositionableEntity.currentPosition: Coordinate
    get() = position.value
    internal set(value) {
        position = value.toProperty()
    }

/**
 * Invokes [whenPositionable] if this entity is an [PositionableEntity]. When the type is not [Positionable],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenPositionable]
 */
fun <R> AnyGameEntity.asPositionableEntity(whenPositionable: (PositionableEntity) -> R): Maybe<R> =
    tryCastTo<PositionableEntity, Positionable, R>(whenPositionable)
