package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.communicator
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.systems.facets.OrderCommand
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioContext
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.base.BaseBehavior
import kotlin.random.Random

internal object Communicating : BaseBehavior<GameContext>(ElementAttribute::class, RadioAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is ElementType) {
            return false
        }
        val elementEntity = entity as ElementEntity
        val radioCommunicator = elementEntity.communicator
        radioCommunicator.radioContext = ElementRadioContext(elementEntity, context)
        radioCommunicator.proceedWithConversation()
        return true
    }

}

/** [RadioContext] that uses an [ElementEntity] for interactions */
class ElementRadioContext(private val element: ElementEntity, private val context: GameContext) : RadioContext {
    // TODO: Fill the context from element. It should be provided by the corresponding game entity (probably via Properties or ObservableValues)
    override fun newTransmissionContext() = TransmissionContext(
            element.currentPosition,
            Random.nextInt(40, 50),
            Random.nextInt(3, 10),
            Random.nextInt(0, 4))

    override fun executeOrder(order: Conversations.Orders, orderedTo: Coordinate?) {
        // TODO use own scope for sending commands async!?
        runBlocking {
            // executed on next tick!
            element.sendCommand(OrderCommand(order, orderedTo, context, element))
        }
    }

}

