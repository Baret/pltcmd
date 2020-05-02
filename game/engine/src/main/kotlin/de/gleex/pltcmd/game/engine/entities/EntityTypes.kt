package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.entities.types.Movable
import org.hexworks.amethyst.api.base.BaseEntityType

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable element."), Movable
