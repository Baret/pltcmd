package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.game.engine.extensions.hasFacet
import de.gleex.pltcmd.game.engine.systems.facets.MakesSecurityHalts
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This goal is the abstract version of "move to this destination". It executes checks to make sure the element
 * reaches its destination in a meaningful manner. It only uses [MoveToGoal] when applicable.
 */
class ReachDestination(private val destination: Coordinate) : Goal() {
    override fun isFinished(element: ElementEntity): Boolean =
            element.position.value == destination

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        if (hasSubGoals().not()) {
            if (element.hasFacet(MakesSecurityHalts::class)) {
                addSubGoals(
                        TimeoutGoal(30, MoveToGoal(destination)),
                        SecurityHalt(5))
            } else {
                addSubGoals(
                        MoveToGoal(destination)
                )
            }
        }
        return stepSubGoals(element, context)
    }

}
