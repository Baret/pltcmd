package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.RadioSignal
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.logging.api.LoggerFactory
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

    private val log = LoggerFactory.getLogger(this::class)

    init {
        strengthProperty.onChange { drawSignal() }
        rangeProperty.onChange { drawSignal() }
    }

    override fun invoke(event: MouseEvent, phase: UIEventPhase): UIEventResponse {
        if(phase == UIEventPhase.TARGET && event.type == MouseEventType.MOUSE_CLICKED) {
            clickedPosition = event.position.minus(mapOffset)
            drawSignal()
        }
        return Pass
    }

    private fun drawSignal() {
        log.debug("Drawing radio signal at position $clickedPosition -> ${world.coordinateAtVisiblePosition(clickedPosition)}")
        reset()
        world.
            fetchBlockAtVisiblePosition(clickedPosition).
            ifPresent {clickedBlock ->
                clickedBlock.setUnit(TileRepository.Elements.PLATOON_FRIENDLY)
                lastBlocks.add(clickedBlock)
                val signal = RadioSignal(strengthProperty.value.toDouble())
                EllipseFactory.
                    buildEllipse(EllipseParameters(clickedPosition, Size.create(rangeProperty.value, rangeProperty.value))).
                    positions.
                    forEach {ringPosition ->
                        val terrainList = mutableListOf(clickedBlock.terrain)
                        LineFactory.
                            buildLine(clickedPosition, ringPosition).
                            drop(1).
                            forEach { linePosition ->
                                world.fetchBlockAtVisiblePosition(linePosition).ifPresent {
                                    terrainList.add(it.terrain)
                                    if(it.hasOverlay().not()) {
                                        it.setOverlay(TileRepository.forSignal(signal.along(terrainList)))
                                        lastBlocks.add(it)
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
