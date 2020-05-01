package de.gleex.pltcmd.game.engine.entities

import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.entity.EntityType

/** Type marker for entities that have the PositionAttribute */
interface Positionable: EntityType

/** Type marker for entities that are [Positionable] and have the DestinationAttribute */
interface Movable: Positionable

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable element."), Movable
