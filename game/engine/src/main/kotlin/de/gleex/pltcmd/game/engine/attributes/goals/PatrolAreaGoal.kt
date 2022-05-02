package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.hexworks.amethyst.api.Message

/**
 * This goal finds random destinations in the given area and moves there. At each waypoint the
 * element makes a [SecurityHalt].
 *
 * This goal is an [EndlessGoal]!
 */
data class PatrolAreaGoal(private val patrolAt: CoordinateArea) : EndlessGoal() {
    override fun step(element: ElementEntity, context: GameContext): Message<GameContext>? {
        if (hasSubGoals().not()) {
            val randomDestination = context
                    .world
                    .moveInside(patrolAt
                            .random(context.random))
            prependSubGoals(
                    ReachDestination(randomDestination),
                    TakeElevatedPosition(randomDestination, context.world),
                    // TODO: Go firm instead of making a security halt
                    SecurityHalt(8)
            )
        }
        return stepSubGoals(element, context)
    }

}
