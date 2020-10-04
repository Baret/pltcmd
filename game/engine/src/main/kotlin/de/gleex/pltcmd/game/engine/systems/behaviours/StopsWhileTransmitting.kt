package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.flags.Transmitting
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.entities.types.Communicating
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * [CommunicatingEntity]s with this behavior need to stop when they are transmitting on the radio.
 *
 * This behavior adds the [Transmitting] flag while the entity [isTransmitting].
 */
object StopsWhileTransmitting : BaseBehavior<GameContext>(RadioAttribute::class) {
    private val log = LoggerFactory.getLogger(StopsWhileTransmitting::class)

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        var updated = false
        if (entity.type is Communicating) {
            @Suppress("UNCHECKED_CAST")
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
