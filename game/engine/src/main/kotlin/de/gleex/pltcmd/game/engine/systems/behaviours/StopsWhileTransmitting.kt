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
import org.hexworks.cobalt.databinding.api.extension.fold

private val log = KotlinLogging.logger {}

/**
 * [CommunicatingEntity]s with this behavior need to stop when they are transmitting on the radio.
 *
 * This behavior adds the [Transmitting] flag while the entity [isTransmitting].
 */
object StopsWhileTransmitting : BaseBehavior<GameContext>(RadioAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.asCommunicatingEntity { communicating ->
            var updated = false
            communicating.findAttributeOrNull(Transmitting::class)
                .fold(whenNull = {
                    if (communicating.isTransmitting) {
                        log.debug { "${communicating.logIdentifier} is transmitting, adding attribute." }
                        communicating.addIfMissing(Transmitting)
                        updated = true
                    }
                }, whenNotNull = {
                    if (communicating.isTransmitting.not()) {
                        log.debug { "${communicating.logIdentifier} stopped transmitting, removing attribute." }
                        communicating.asMutableEntity()
                            .removeAttribute(Transmitting)
                        updated = true
                    }
                })
            updated
        } ?: false
    }

}
