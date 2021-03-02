package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.knowledge.LocatedContact
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.signals.vision.Visibility
import org.hexworks.amethyst.api.Message

/**
 * Message that [source] has contact with another [GameEntity]. The fields determine whether it is a new or known
 * contact and how much information can be retrieved from the contact.
 *
 * @param contact information about an element that is in the visible range of [source].
 * @param visibility how well the contact can be identified
 * @param previousVisibility if same contact was already made last update tick, then the old value else [Visibility.NONE]
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectedEntity(
    val contact: LocatedContact,
    private val visibility: Visibility,
    private val previousVisibility: Visibility = Visibility.NONE,
    override val source: SeeingEntity,
    override val context: GameContext
) : Message<GameContext> {

    val increasedVisibility = visibility > previousVisibility
}
