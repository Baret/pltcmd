package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.destination
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.game.engine.messages.MoveTo
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import java.util.*

object PathFinding : BaseFacet<GameContext, MoveTo>(MoveTo::class) {

    override suspend fun receive(message: MoveTo): Response {
        val (destination, _, entity) = message
        if (entity.movementPath.isNotEmpty()
            && entity.destination.filter { current -> current == destination }.isPresent) {
            return Consumed
        }
        val currentPosition = entity.position.value
        // TODO: Use a PathFinder (new interface) here. It should be held by a mutable Attribute so depending on its state an element can use different PathFinders
        entity.movementPath = Stack<Coordinate>()
            .apply {
                addAll(
                    CoordinatePath
                        .line(currentPosition, destination)
                        .drop(1)
                        .reversed()
                )
            }
        return Consumed
    }
}
