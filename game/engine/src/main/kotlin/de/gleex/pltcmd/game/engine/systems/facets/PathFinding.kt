package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.commands.SetDestination
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.geometry.pointsOfLine
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
        return command.responseWhenCommandIs(SetDestination::class) {
            val (destination, _, entity) = command as SetDestination
            val pathStack = Stack<Coordinate>()
            val currentPosition = entity.position.value
            log.debug("Finding path from $currentPosition to $destination...")
            pointsOfLine(currentPosition.eastingFromLeft, currentPosition.northingFromBottom, destination.eastingFromLeft, destination.northingFromBottom) { x, y ->
                pathStack.push(Coordinate(x, y))
            }
            pathStack.reverse()
            log.debug("Setting path: $pathStack")
            entity.movementPath = pathStack
            Consumed
        }
    }
}