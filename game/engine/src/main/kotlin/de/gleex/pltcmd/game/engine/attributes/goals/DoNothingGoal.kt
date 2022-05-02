package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Message

/**
 * This goal simply does nothing. It may be wrapped into an [TimeoutGoal] to let an entity wait for a number of ticks.
 */
object DoNothingGoal: EndlessGoal() {
    override fun step(element: ElementEntity, context: GameContext): Message<GameContext>? {
        return null
    }
}