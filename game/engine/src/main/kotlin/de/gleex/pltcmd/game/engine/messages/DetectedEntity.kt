package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.memory.Contact
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
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
    val contact: Contact,
    override val source: SeeingEntity,
    override val context: GameContext
) : Message<GameContext>