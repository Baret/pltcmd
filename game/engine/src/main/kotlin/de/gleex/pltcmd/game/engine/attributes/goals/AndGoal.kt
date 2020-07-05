package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.CommandResponse

class AndGoal(private val goal: Goal, private val nextGoal: Goal) : Goal() {
    override fun step(element: ElementEntity, context: GameContext): CommandResponse<GameContext> {
        if(goal.isFinished(element)) {
            nextGoal.step()
        } else {
            goal.step()
        }
    }
}
