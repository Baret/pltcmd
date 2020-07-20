package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command


/** Command to provide entities a destination **/
data class MoveTo(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: MovableEntity
) : Command<Movable, GameContext>