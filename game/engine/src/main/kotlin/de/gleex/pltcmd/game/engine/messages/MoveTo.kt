package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Message


/** Message to provide entities a destination **/
data class MoveTo(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: MovableEntity
) : Message<GameContext>