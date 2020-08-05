package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.Entity

/** Command that is fired when the position of an entity changes. It contains the old and new position. */
data class UpdatePosition(
        val oldPosition: Coordinate,
        val newPosition: Coordinate,
        override val context: GameContext,
        override val source: Entity<Movable, GameContext>
) : Command<Movable, GameContext>
