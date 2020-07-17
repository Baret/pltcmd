package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.Entity

data class SetDestination(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: Entity<Movable, GameContext>
): Command<Movable, GameContext>