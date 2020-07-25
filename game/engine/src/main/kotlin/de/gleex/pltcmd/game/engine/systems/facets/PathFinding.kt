package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.commands.MoveTo
import de.gleex.pltcmd.game.engine.entities.types.destination
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import java.util.*

object PathFinding : BaseFacet<GameContext>() {

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response {
        return command.responseWhenCommandIs(MoveTo::class) {
            val (destination, _, entity) = it
            if(entity.movementPath.isNotEmpty()
                    && entity.destination.filter { current -> current == destination }.isPresent) {
                return@responseWhenCommandIs Consumed
            }
            val currentPosition = entity.position.value
            val pathStack = Stack<Coordinate>()
                    .apply {
                        addAll(
                            CoordinatePath
                                .line(currentPosition, destination)
                                .drop(1)
                                .reversed())
                    }
            entity.movementPath = pathStack
            Consumed
        }
    }
}