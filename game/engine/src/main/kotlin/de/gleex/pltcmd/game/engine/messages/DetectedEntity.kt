package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.signals.vision.Visibility
import org.hexworks.amethyst.api.Message

/**
 * Message that [source] has contact with another [GameEntity]. The fields determine whether it is a new or known
 * contact and how much information can be retrieved from the contact.
 *
 * @param entity entity that is in the visible range of [source].
 * @param visibility how well the contact can be identified
 * @param previousVisibility if same contact was already made last update tick, then the old value else [Visibility.NONE]
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectedEntity(
    val entity: PositionableEntity,
    val visibility: Visibility,
    val previousVisibility: Visibility,
    override val source: SeeingEntity,
    override val context: GameContext
) : Message<GameContext> {

    val increasedVisibility = visibility > previousVisibility
}
