package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.goals.SecurityHalt
import de.gleex.pltcmd.game.engine.commands.UpdatePosition
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.game.engine.entities.types.baseSpeedInKph
import de.gleex.pltcmd.game.engine.entities.types.currentSpeedInKph
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.model.world.toSectorOrigin
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * When entering a new sector this facet plans a [SecurityHalt] after some tiles.
 */
object MakesSecurityHalts : BaseFacet<GameContext>() {

    private val log = LoggerFactory.getLogger(MakesSecurityHalts::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(UpdatePosition::class) {
                if (it.oldPosition.toSectorOrigin() != it.newPosition.toSectorOrigin()) {
                    val entity: MovableEntity = it.source as MovableEntity
                    entity.findAttribute(CommandersIntent::class)
                            .ifPresent { intent ->
                                // Make a security halt when approximately 300m into the new sector
                                val afterTiles = 3.0
                                val ticksPerTile = 6.0 / if (entity.currentSpeedInKph > 0.0) entity.currentSpeedInKph else entity.baseSpeedInKph
                                val inTurns = (afterTiles * ticksPerTile).toInt()

                                if (entity.movementPath.size > afterTiles) {
                                    log.debug("- - - Entered new sector! Planning security halt at Tick ${Ticker.currentTick + inTurns}")
                                    intent.inTurns(inTurns, SecurityHalt(3))
                                }
                            }
                }
                Pass
            }
}
