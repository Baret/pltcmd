package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * This file contains code for entities that have the RadioAttribute.
 */

/** Type marker for entities that have the [RadioAttribute] */
interface Communicatable : EntityType
typealias CommunicatableEntity = GameEntity<Communicatable>

private val log = LoggerFactory.getLogger(Communicatable::class)

internal val CommunicatableEntity.communicator: RadioCommunicator
    get() = getAttribute(RadioAttribute::class).communicator

/** TODO is visible as a debug feature for the test UI, might be removed later */
val CommunicatableEntity.inConversationWith: ObservableValue<Maybe<CallSign>>
    get() = communicator.inConversationWith

internal fun CommunicatableEntity.startConversation(conversation: Conversation) {
    log.debug("${(this as ElementEntity).callsign} starting conversation $conversation")
    communicator.startConversation(conversation)
}
