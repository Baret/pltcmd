package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.knowledge.LocatedContact
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import de.gleex.pltcmd.game.engine.entities.types.asRememberingEntity
import de.gleex.pltcmd.game.engine.entities.types.isKnown
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import org.hexworks.amethyst.api.Message

/**
 * Message that [source] has contact with another [GameEntity]. The fields determine whether it is a new or known
 * contact and how much information can be retrieved from the contact.
 *
 * @param contact information about an element that is in the visible range of [source].
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectedEntity(
    val contact: LocatedContact,
    override val source: SeeingEntity,
    override val context: GameContext
) : Message<GameContext> {

    /** checks the memory for the amount already known about this contact */
    val isKnown: Boolean
        get() = source.asRememberingEntity {
            it.isKnown(contact)
        }.orElse(false)
}
