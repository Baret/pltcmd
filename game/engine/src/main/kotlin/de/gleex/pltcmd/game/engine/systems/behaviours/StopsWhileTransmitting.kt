package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.flags.Transmitting
import de.gleex.pltcmd.game.engine.entities.types.CommunicatingEntity
import de.gleex.pltcmd.game.engine.entities.types.asCommunicatingEntity
import de.gleex.pltcmd.game.engine.entities.types.isTransmitting
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * [CommunicatingEntity]s with this behavior need to stop when they are transmitting on the radio.
 *
 * This behavior adds the [Transmitting] flag while the entity [isTransmitting].
 */
object StopsWhileTransmitting : BaseBehavior<GameContext>(RadioAttribute::class) {
    private val log = KotlinLogging.logger {}

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.asCommunicatingEntity { communicating ->
            var updated = false
            communicating.findAttribute(Transmitting::class)
                .fold(whenEmpty = {
                    if (communicating.isTransmitting) {
                        log.debug { "${communicating.logIdentifier} is transmitting, adding attribute." }
                        communicating.addIfMissing(Transmitting)
                        updated = true
                    }
                }, whenPresent = {
                    if (communicating.isTransmitting.not()) {
                        log.debug { "${communicating.logIdentifier} stopped transmitting, removing attribute." }
                        communicating.asMutableEntity()
                            .removeAttribute(Transmitting)
                        updated = true
                    }
                })
            updated
        }.orElseGet {
            false
        }
    }

}
