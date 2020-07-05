package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.position
import org.hexworks.amethyst.api.CommandResponse

/**
 * A "dummy goal" that is always finished.
 */
class EmptyGoal : Goal() {
    override fun isFinished(element: ElementEntity): Boolean = true

    override fun step(element: ElementEntity, context: GameContext): CommandResponse<GameContext> {
        return MoveToGoal(element.position.value).step(element, context)
    }

}
