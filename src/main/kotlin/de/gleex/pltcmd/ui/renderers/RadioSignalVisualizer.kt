package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.RadioSignal
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.EllipseParameters
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.*

/**
 * A mouse listener that creates an overlay displaying a radio signal.
 */
class RadioSignalVisualizer(
        private val world: GameWorld,
        private val strengthProperty: Property<Int>,
        private val rangeProperty: Property<Int>,
        private val mapOffset: Position) : (MouseEvent, UIEventPhase) -> UIEventResponse {

    private var clickedPosition = Position.defaultPosition()
    private val lastBlocks = mutableSetOf<MapBlock>()

    init {
        strengthProperty.onChange { drawSignal() }
        rangeProperty.onChange { drawSignal() }
        GameOptions.attenuationModel.onChange { drawSignal() }
        GameOptions.displayRadioSignals.onChange { reset() }
    }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse {
        if(phase == UIEventPhase.TARGET && event.type == MouseEventType.MOUSE_CLICKED) {
            val newPosition = event.position.minus(mapOffset)
            if(clickedPosition == newPosition && lastBlocks.isNotEmpty()) {
                reset()
            } else {
                clickedPosition = newPosition
                drawSignal()
            }
        }
        return Pass
    }

    private fun drawSignal() {
        reset()
        if(GameOptions.displayRadioSignals.value) {
            world.fetchBlockAtVisiblePosition(clickedPosition).
                    ifPresent { clickedBlock ->
                        clickedBlock.setOverlay(TileRepository.Elements.PLATOON_FRIENDLY)
                        lastBlocks.add(clickedBlock)
                        val signal = RadioSignal(strengthProperty.value.toDouble())
                        // To not miss any tiles we create growing ellipses...
                        for (circleRadius in 1..rangeProperty.value) {
                            EllipseFactory.buildEllipse(EllipseParameters(clickedPosition, Size.create(circleRadius, circleRadius))).
                                positions.
                                forEach { ringPosition ->
                                // and draw lines from the center to every position on the circle
                                // (When drawing lines to very large circles the lines are too thin to cover every position within the circle)
                                // (And Zircon does not seem to know a filled ellipse...)
                                val terrainList = mutableListOf(clickedBlock.terrain)
                                LineFactory.buildLine(clickedPosition, ringPosition)
                                    .drop(1)
                                    .forEach { linePosition ->
                                        world.fetchBlockAtVisiblePosition(linePosition)
                                            .ifPresent {
                                                terrainList.add(it.terrain)
                                                if (it.hasOverlay().not()) {
                                                    it.setOverlay(TileRepository.forSignal(signal.along(terrainList)))
                                                    lastBlocks.add(it)
                                                }
                                            }
                                    }
                            }
                        }
                    }
        }
    }

    private fun reset() {
        lastBlocks.forEach { it.resetOverlay() }
        lastBlocks.clear()
    }
}
