package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.Seeing
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import org.hexworks.amethyst.api.Command

/**
 * Command that [source] has contact with another [ElementEntity].
 *
 * @param element element that is in the visible range of [source].
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectedElement(val element: ElementEntity,
                           override val source: SeeingEntity,
                           override val context: GameContext) : Command<Seeing, GameContext>
