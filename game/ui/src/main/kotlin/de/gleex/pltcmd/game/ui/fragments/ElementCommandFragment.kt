package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.messages.ConversationMessage
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.Conversations.Orders.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.dsl.fragment.buildSelector
import org.hexworks.zircon.api.uievent.*

private val log = KotlinLogging.logger {}

/**
 * Displays a list of entities and makes it possible to send them a command from `hq`.
 * Currently they get a move command sent by the given `hq`. For now this fragment is just a debug/playaround
 * feature. But it may be used as the base for the UI element used to send radio messages to elements.
 */
class ElementCommandFragment(
        override val fragmentWidth: Int,
        private val world: GameWorld,
        private val hq: FOBEntity,
        elements: List<ElementEntity>,
        private val mapOffset: Property<Position>,
        private val game: Game
) : BaseFragment, (MouseEvent, UIEventPhase) -> UIEventResponse {

    private val elementSelect = buildSelector<ElementEntity> {
        width = fragmentWidth
        valueList = elements
        toStringMethod = { it.callsign.toString() }
    }

    private val selectedDestination = elements.first().currentPosition.toProperty()
    private val selectedCallsign: ObservableValue<CallSign> = elementSelect
        .selectedValue
        .bindTransform { it.callsign }

    override val root = Components.vbox()
            .withSize(fragmentWidth, 5)
            .build()
            .apply {
                addFragment(elementSelect)
                addComponent(Components.button()
                        .withSize(width, 1)
                        .build()
                        .apply {
                            textProperty.updateFrom("Move to ".toProperty() bindPlusWith selectedDestination.bindTransform { it.toString() }, true)
                            onActivated {
                                sendOrder(MoveTo)
                            }
                        })
                addComponent(Components.button()
                        .withSize(width, 1)
                        .build()
                        .apply {
                            textProperty.updateFrom("Patrol at ".toProperty() bindPlusWith selectedDestination.bindTransform { it.toString() }, true)
                            onActivated {
                                sendOrder(PatrolAreaAt)
                            }
                        })
                addComponent(Components.button()
                        .withSize(width, 1)
                        .withText("Halt!")
                        .build()
                        .apply {
                            onActivated {
                                sendConversation(Halt.create(hq.radioCallSign, selectedCallsign.value))
                            }
                        })
                addComponent(Components.button()
                        .withSize(width, 1)
                        .withText("Continue!")
                        .build()
                        .apply {
                            onActivated {
                                sendOrder(Continue)
                            }
                        })
            }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse =
            if (phase == UIEventPhase.TARGET && event.button == 1) {
                val coord = world.coordinateAtVisiblePosition(event.position - mapOffset.value)
                log.debug { "MOUSE CLICKED at ${event.position}! Offset is $mapOffset. Updating command fragment value to $coord" }
                selectedDestination.updateValue(coord)
                Processed
            } else {
                Pass
            }

    private fun sendOrder(order: Conversations.Orders) =
            sendConversation(order.create(hq.radioCallSign, selectedCallsign.value, selectedDestination.value))

    private fun sendConversation(conversation: Conversation) {
        log.debug { "Sending conversation to ${conversation.receiver}: $conversation" }
        runBlocking {
            hq.sendMessage(ConversationMessage(conversation, game.context(), hq))
        }
    }

}
