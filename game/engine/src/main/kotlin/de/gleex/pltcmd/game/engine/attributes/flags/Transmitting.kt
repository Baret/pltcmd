package de.gleex.pltcmd.game.engine.attributes.flags

import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier

/**
 * When this flag is present the entity can not move because it is currently in a radio conversation.
 */
object Transmitting: MovementModifier.Prevention()
