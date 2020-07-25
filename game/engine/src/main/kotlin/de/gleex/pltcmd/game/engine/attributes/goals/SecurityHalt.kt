package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.flags.Halted
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

class SecurityHalt(turnsToHalt: Int): Goal() {

    private var turnsRemaining: Int = turnsToHalt + 1

    private val log = LoggerFactory.getLogger(SecurityHalt::class)

    override fun isFinished(element: ElementEntity): Boolean =
            turnsRemaining <= 0

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        element.addIfMissing(Halted)
        turnsRemaining--
        log.debug("HALT! Making SECURITY HALT for another $turnsRemaining")
        if(turnsRemaining <= 0) {
            log.debug("FORWARD! Security halt finished.")
            element.asMutableEntity().removeAttribute(Halted)
        }
        return Maybe.empty()
    }
}
