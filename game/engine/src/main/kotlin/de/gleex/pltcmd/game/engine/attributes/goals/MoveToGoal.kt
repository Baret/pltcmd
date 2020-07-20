package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.commands.MoveTo
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

class MoveToGoal(private val destination: Coordinate): Goal() {
    override fun isFinished(element: ElementEntity): Boolean =
            element.position.value == destination

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return Maybe.of(MoveTo(destination, context, element))
    }
}