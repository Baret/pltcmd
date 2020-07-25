package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.goals.SecurityHalt
import de.gleex.pltcmd.game.engine.commands.UpdatePosition
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.currentSpeedInKph
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.model.world.toSectorOrigin
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

object MakesSecurityHalts : BaseFacet<GameContext>() {

    private val log = LoggerFactory.getLogger(MakesSecurityHalts::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(UpdatePosition::class) {
                if(it.oldPosition.toSectorOrigin() != it.newPosition.toSectorOrigin()) {
                    it.source.findAttribute(CommandersIntent::class).ifPresent { intent ->
                        // Make a security halt when approximately 300m into the new sector
                        val inTurns = if(command.source.type == ElementType) {
                            val element = command.source as ElementEntity
                            val ticksPerTile = 6.0 / element.currentSpeedInKph
                            (3.0 * ticksPerTile).toInt()
                        } else {
                            3
                        }
                        log.info("Entered new sector! Planning security halt at Tick ${Ticker.currentTick + inTurns}")
                        intent.inTurns(inTurns, SecurityHalt(2))
                    }
                }
                Pass
            }
}
