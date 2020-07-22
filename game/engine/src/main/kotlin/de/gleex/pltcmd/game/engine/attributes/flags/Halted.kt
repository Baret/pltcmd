package de.gleex.pltcmd.game.engine.attributes.flags

import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier

object Halted: MovementModifier {
    override val type = MovementModifier.Type.Prevention
}