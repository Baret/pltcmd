package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.communication.RadioCommunicator
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.position
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.radio.communication.Conversations
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.api.uievent.UIEventResponse

/**
 * Displays a list of entities and makes it possible to send them a command from `hq`.
 * Currently they get a move command send by the given `hq`. For now this fragment is just a debug/playaround
 * feature. But it may be used as the base for the UI element used to send radio commands to elements.
 */
class ElementCommandFragment(override val width: Int, private val world: GameWorld, val hq: RadioCommunicator, elements: List<ElementEntity>, private val mapOffset: Position) : BaseFragment, (MouseEvent, UIEventPhase) -> UIEventResponse {

    private var selectedElement: ElementEntity = elements.first()
    private val destinationProperty = createPropertyFrom(selectedElement.position.value)
    private val elementSelect = Fragments.
                                    multiSelect(width, elements).
                                    withDefaultSelected(selectedElement).
                                    withCallback{_, newElement -> selectedElement = newElement}.
                                    withToStringMethod { it.callsign.toString() }.
                                    build()

    companion object {
        private val log = LoggerFactory.getLogger(ElementCommandFragment::class)
    }

    override val root = Components.vbox().
            withSize(width, 3).
            build().
            apply {
                addFragment(elementSelect)
                addComponent(Components.
                    label().
                    withSize(width, 1).
                    build().
                    apply {
                        textProperty.updateFrom(createPropertyFrom("Move to ") bindPlusWith destinationProperty.bindTransform { it.toString() }, true)
                    })
                addComponent(Components.
                    button().
                    withSize(width, 1).
                    withText("Send command").
                    build().
                    apply {
                        onActivated {
                            sendMoveTo()
                        }
                    })
            }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse {
        val coord = world.coordinateAtVisiblePosition(event.position.minus(mapOffset))
        destinationProperty.updateValue(coord)
        return UIEventResponse.processed()
    }

    private fun sendMoveTo() {
        val conversation = Conversations.Orders.moveTo(hq.callSign, selectedElement.callsign, destinationProperty.value)
        log.info("Sending conversation to ${conversation.receiver}: $conversation")
        hq.startCommunication(conversation)
    }

}