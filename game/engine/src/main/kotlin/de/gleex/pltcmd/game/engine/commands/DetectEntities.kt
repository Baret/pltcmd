package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.SeeingEntity
import org.hexworks.amethyst.api.Command

/**
 * Command to tell [source] to look for [visibleEntities].
 *
 * @param visibleEntities entities that are in the visible range of [source].
 * @param source the [SeeingEntity] that might need to react to the detection of other entities.
 */
data class DetectEntities(val visibleEntities: EntitySet<Positionable>,
                          override val context: GameContext,
                          override val source: SeeingEntity) : Command<Positionable, GameContext>