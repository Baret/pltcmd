package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.Entity

data class UpdatePosition(
        val oldPosition: Coordinate,
        val newPosition: Coordinate,
        override val context: GameContext,
        override val source: Entity<Positionable, GameContext>
) : Command<Positionable, GameContext>
