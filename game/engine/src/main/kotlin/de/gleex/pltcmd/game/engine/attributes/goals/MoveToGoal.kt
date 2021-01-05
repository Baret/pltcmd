package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.destination
import de.gleex.pltcmd.game.engine.entities.types.hasNoDestination
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.game.engine.messages.MoveTo
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Message
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Checks if the element is currently moving to [destination]. If not it returns a [MoveTo] message.
 *
 * This goal should not be set directly, as it is rather dumb. Use [ReachDestination] to make sure the element will
 * eventually get to the destination.
 */
data class MoveToGoal(private val destination: Coordinate): Goal() {
    override fun isFinished(element: ElementEntity): Boolean =
            element.position.value == destination

    override fun step(element: ElementEntity, context: GameContext): Maybe<Message<GameContext>> {
        return if(element.hasNoDestination || element.destination.get() != destination) {
            Maybe.of(MoveTo(destination, context, element))
        } else {
            Maybe.empty()
        }
    }
}