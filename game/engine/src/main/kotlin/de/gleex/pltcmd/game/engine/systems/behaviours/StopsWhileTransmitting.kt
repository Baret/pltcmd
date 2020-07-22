package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.flags.Transmitting
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.entities.types.Communicating
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

object StopsWhileTransmitting : BaseBehavior<GameContext>(RadioAttribute::class) {
    private val log = LoggerFactory.getLogger(StopsWhileTransmitting::class)

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        var updated = false
        if (entity.type is Communicating) {
            entity as CommunicatingEntity
            entity.findAttribute(Transmitting::class)
                    .fold({
                        if(entity.isTransmitting) {
                            log.info("${(entity as ElementEntity).callsign} is transmitting, adding attribute.")
                            entity.asMutableEntity().addAttribute(Transmitting)
                            updated = true
                        }
                    }, {
                        if(entity.isTransmitting.not()) {
                            log.info("${(entity as ElementEntity).callsign} stopped transmitting, removing attribute.")
                            entity.asMutableEntity().removeAttribute(Transmitting)
                            updated = true
                        }
                    })
        }
        return updated
    }

}
