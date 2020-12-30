package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import de.gleex.pltcmd.game.engine.entities.types.Seeing
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import org.hexworks.amethyst.api.Command

/**
 * Command that [source] has contact with another unknown [GameEntity].
 *
 * @param entity entity that is in the visible range of [source] but cannot be identified further.
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectedUnknown(val entity: PositionableEntity,
                           override val source: SeeingEntity,
                           override val context: GameContext) : Command<Seeing, GameContext>
