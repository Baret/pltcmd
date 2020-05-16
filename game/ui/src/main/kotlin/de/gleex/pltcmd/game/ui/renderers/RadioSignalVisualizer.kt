package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.radio.broadcasting.AttenuationModel
import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.EllipseParameters
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.*

/**
 * A mouse listener that creates an overlay displaying a radio signal.
 *
 * @param world The world is used to find the terrain the [RadioSignal] travels along.
 * @param strengthProperty When this property is updated a redraw is triggered
 * @param rangeProperty When this property is updated a redraw is triggered
 * @param mapOffset The position of the map in the UI. Mouse events carry the absolute position in the whole window so
 *                  we need this offset to calculate the position on the map view.
 */
class RadioSignalVisualizer(
        private val world: GameWorld,
        private val strengthProperty: Property<Int>,
        private val rangeProperty: Property<Int>,
        private val mapOffset: Position) : (MouseEvent, UIEventPhase) -> UIEventResponse {

    private var clickedPosition = Position.defaultPosition()
    private val lastBlocks = mutableSetOf<GameBlock>()

    init {
        strengthProperty.onChange { drawSignal() }
        rangeProperty.onChange { drawSignal() }
        AttenuationModel.DEFAULT.onChange { drawSignal() }
        GameOptions.displayRadioSignals.onChange { reset() }
    }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse {
        if(phase == UIEventPhase.TARGET
                && event.type == MouseEventType.MOUSE_CLICKED
                && drawingAllowed()) {
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
        if (drawingAllowed().not()) {
            return
        }
        world.fetchBlockAtVisiblePosition(clickedPosition).
                ifPresent(this::buildCirclesAround)
    }

    private fun drawingAllowed() = GameOptions.displayRadioSignals.value

    private fun buildCirclesAround(clickedBlock: GameBlock) {
        clickedBlock.setOverlay(TileRepository.Elements.platoon(Affiliation.Friendly))
        lastBlocks.add(clickedBlock)
        val signal = RadioSignal(strengthProperty.value.toDouble())
        // To not miss any tiles we create growing ellipses...
        for (circleRadius in 1..rangeProperty.value) {
            EllipseFactory.
                    buildEllipse(EllipseParameters(clickedPosition, Size.create(circleRadius, circleRadius))).
                    positions.
                    forEach { ringPosition ->
                        // and draw lines from the center to every position on the circle
                        // (When drawing lines to very large circles the lines are too thin to cover every position within the circle)
                        // (And Zircon does not seem to know a filled ellipse...)
                        drawLine(clickedBlock, ringPosition, signal)
            }
        }
    }

    private fun drawLine(clickedBlock: GameBlock, ringPosition: Position, signal: RadioSignal) {
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

    private fun reset() {
        lastBlocks.forEach(GameBlock::resetOverlay)
        lastBlocks.clear()
    }
}
