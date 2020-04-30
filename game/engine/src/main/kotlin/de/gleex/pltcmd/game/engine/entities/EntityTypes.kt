package de.gleex.pltcmd.game.engine.entities

import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.entity.EntityType

interface Movable: EntityType

interface Positionble: EntityType

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable element."), Movable, Positionble
