package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.Positionable
import de.gleex.pltcmd.game.engine.entities.types.PositionableEntity
import org.hexworks.amethyst.api.Command

data class DetectEntities(val visibleEntities: EntitySet<Positionable>,
                          override val context: GameContext,
                          override val source: PositionableEntity) : Command<Positionable, GameContext>
