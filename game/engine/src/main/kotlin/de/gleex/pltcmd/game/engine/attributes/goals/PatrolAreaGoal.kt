package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

class PatrolAreaGoal(private val patrolAt: CoordinateArea) : Goal() {
    var nextDestination: Coordinate? = null

    companion object {
        private val log = LoggerFactory.getLogger(PatrolAreaGoal::class)
    }

    init {
        log.info("Created goal PATROL AREA: $patrolAt")
    }

    override fun isFinished(element: ElementEntity): Boolean {
        return false
    }

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        // TODO: add "and wait some turns" after moving
        if (nextDestination == null) {
            nextDestination = context.world.moveInside(patrolAt.toSet().random())
            log.info("No destination. Found random destination $nextDestination")
        }
        val reachDestinationGoal = ReachDestination(nextDestination!!)
        return if (reachDestinationGoal.isFinished(element)) {
            log.info("Destination $nextDestination reached. Resetting destination.")
            nextDestination = null
            Maybe.empty()
        } else {
            log.info("Stepping ReachDestination towards $nextDestination")
            reachDestinationGoal.step(element, context)
        }
    }

}
