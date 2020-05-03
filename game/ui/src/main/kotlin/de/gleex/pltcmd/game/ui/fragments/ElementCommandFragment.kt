package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.engine.attributes.callsign
import de.gleex.pltcmd.game.engine.entities.ElementType
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.world.Sector
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.internal.binding.ComputedBinding
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.api.uievent.UIEventResponse
import kotlin.random.Random

/**
 * Displays a list of entities and makes it possible to send them a
 */
class ElementCommandFragment(override val width: Int, private val world: GameWorld, elements: List<GameEntity<ElementType>>) : BaseFragment, (MouseEvent, UIEventPhase) -> UIEventResponse {

    private val destinationProperty = createPropertyFrom(world.visibleTopLeftCoordinate().movedBy(Random.nextInt(Sector.TILE_COUNT), Random.nextInt(Sector.TILE_COUNT)))
    private var selectedElement: GameEntity<ElementType> = elements.first()
    private val elementSelect = Fragments.
                                    multiSelect(width, elements).
                                    withDefaultSelected(selectedElement).
                                    withCallback{_, newElement -> selectedElement = newElement}.
                                    withToStringMethod { it.callsign.toString() }.
                                    build()

    private val log = LoggerFactory.getLogger(ElementCommandFragment::class)

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
                        textProperty.updateFrom(createPropertyFrom("Move to ") bindPlusWith ComputedBinding(destinationProperty) {it.toString()}, true)
                    })
                addComponent(Components.
                    button().
                    withSize(width, 1).
                    withText("Send command").
                    also {
//                        onActivated { selectedElement.sendCommand(MoveTo(destinationProperty.value, GameContext(Ticker.currentTick.value, selectedElement))) }
                    })
            }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse {
        val coord = world.coordinateAtVisiblePosition(event.position)
        log.debug("Coordinate at position ${event.position} is $coord")
        destinationProperty.updateValue(coord)
        return UIEventResponse.processed()
    }
}