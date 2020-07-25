package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.flags.Transmitting
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.entities.types.Communicating
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
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
                    .fold(whenEmpty = {
                        if (entity.isTransmitting) {
                            log.debug("${(entity as ElementEntity).callsign} is transmitting, adding attribute.")
                            entity.addIfMissing(Transmitting)
                            updated = true
                        }
                    }, whenPresent = {
                        if (entity.isTransmitting.not()) {
                            log.debug("${(entity as ElementEntity).callsign} stopped transmitting, removing attribute.")
                            entity.asMutableEntity()
                                    .removeAttribute(Transmitting)
                            updated = true
                        }
                    })
        }
        return updated
    }

}
