package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.geometry.pointsOfLine
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

class ReachDestination(private val destination: Coordinate) : Goal() {
    override fun isFinished(element: ElementEntity): Boolean =
            element.position.value == destination

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        val nextStep = firstStepOfPath(element.position.value, destination)
        return MoveToGoal(nextStep).step(element, context)
    }

    private fun firstStepOfPath(from: Coordinate, destination: Coordinate): Coordinate {
        var nextStep: Coordinate? = null
        pointsOfLine(
                from.eastingFromLeft, from.northingFromBottom,
                destination.eastingFromLeft, destination.northingFromBottom)
            { easting, northing ->
                nextStep = Coordinate(easting, northing)
                return@pointsOfLine
            }
        return nextStep!!
    }
}
