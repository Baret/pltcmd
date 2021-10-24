package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.goals.SecurityHalt
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.game.engine.entities.types.baseSpeed
import de.gleex.pltcmd.game.engine.entities.types.currentSpeed
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.engine.messages.UpdatePosition
import de.gleex.pltcmd.game.options.GameConstants
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.util.measure.speed.Speed
import mu.KotlinLogging
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet

private val log = KotlinLogging.logger {}

/**
 * When entering a new sector this facet plans a [SecurityHalt] after some tiles.
 */
object MakesSecurityHalts : BaseFacet<GameContext, UpdatePosition>(UpdatePosition::class) {

    override suspend fun receive(message: UpdatePosition): Response {
        if (message.oldPosition.sectorOrigin != message.newPosition.sectorOrigin) {
            val entity: MovableEntity = message.source
            entity.findAttribute(CommandersIntent::class)
                .ifPresent { intent ->
                    // Make a security halt when approximately 300m into the new sector
                    val afterTiles = 3.0
                    val ticksPerTile =
                        GameConstants.Movement.speedForOneTileInOneTick / if (entity.currentSpeed > Speed.ZERO) entity.currentSpeed else entity.baseSpeed
                    val inTurns = (afterTiles * ticksPerTile).toInt()

                    if (entity.movementPath.size > afterTiles) {
                        log.debug { "- - - Entered new sector! Planning security halt at Tick ${Ticker.currentTick + inTurns}" }
                        intent.inTurns(inTurns, SecurityHalt(3))
                    }
                }
        }
        return Pass
    }
}
