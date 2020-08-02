package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.flags.Halted
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Sets the [Halted] flag on the given entity if not yet present. This goal is an [EndlessGoal]!
 */
object HaltGoal : EndlessGoal() {
    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        element.addIfMissing(Halted)
        return Maybe.empty()
    }

    /**
     * Sets the status of the given entity back to "not halted".
     */
    fun cleanUp(element: ElementEntity) {
        element.asMutableEntity().removeAttribute(Halted)
    }
}