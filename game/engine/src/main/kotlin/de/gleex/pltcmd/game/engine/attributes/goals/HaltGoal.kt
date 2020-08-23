package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.flags.Halted
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import de.gleex.pltcmd.game.engine.extensions.hasAttribute
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Sets the [Halted] flag on the given entity if not yet present and waits for it to be removed.
 */
class HaltGoal : Goal() {
    companion object {
        /**
         * Sets the status of the given entity back to "not halted" (removes [Halted] flag).
         */
        fun cleanUp(element: ElementEntity) {
            element.asMutableEntity()
                    .removeAttribute(Halted)
        }
    }

    private var started = false

    override fun isFinished(element: ElementEntity): Boolean =
            started && element.hasAttribute(Halted::class)
                    .not()

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        started = true
        element.addIfMissing(Halted)
        return Maybe.empty()
    }
}