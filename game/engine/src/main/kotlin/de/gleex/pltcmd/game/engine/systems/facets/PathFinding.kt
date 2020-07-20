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
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*

object PathFinding : BaseFacet<GameContext>() {

    private val log = LoggerFactory.getLogger(PathFinding::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response {
        return command.responseWhenCommandIs(MoveTo::class) {
            val (destination, _, entity) = it
            log.info("Current destination: ${entity.destination}")
            if(entity.movementPath.isNotEmpty()
                    && entity.destination.filter { current -> current == destination }.isPresent) {
                log.debug("Destination and path are already set.")
                return@responseWhenCommandIs Consumed
            }
            val currentPosition = entity.position.value
            log.debug("Finding path from $currentPosition to $destination...")
            val pathStack = Stack<Coordinate>()
                    .apply {
                        addAll(
                            CoordinatePath
                                .line(currentPosition, destination)
                                .drop(1)
                                .reversed())
                    }
            log.debug("Setting path: $pathStack")
            log.debug("Fist step: ${pathStack.peek()}")
            entity.movementPath = pathStack
            Consumed
        }
    }
}