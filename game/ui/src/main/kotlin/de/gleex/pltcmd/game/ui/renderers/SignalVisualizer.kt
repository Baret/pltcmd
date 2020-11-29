package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.world.coordinate.Coordinate

/**
 * Draws a [Signal] at its origin when activated. When deactivated the last drawn overlays are cleared.
 */
class SignalVisualizer(val world: GameWorld) {

    private var currentSignal: Signal<*>? = null
    private var lastDrawnMap: Map<Coordinate, SignalStrength> = emptyMap()

    /**
     * Clears the drawn overlays so that no signal is visible anymore.
     */
    fun deactivate() {
        clear()
        currentSignal = null
    }

    private fun clear() {
        lastDrawnMap.forEach { (coordinate, _) ->
            world.resetOverlayAt(coordinate)
        }
    }

    /**
     * Draws the given signal at its origin.
     */
    fun activateWith(signal: Signal<*>) {
        deactivate()
        draw(signal.signalMap)
    }

    private fun draw(signalMap: Map<Coordinate, SignalStrength>) {
        signalMap.forEach { (coordinate, signalStrength) ->
            world.fetchBlockAt(coordinate)
                    .ifPresent { block ->
                        block.setOverlay(TileRepository.forSignal(signalStrength))
                    }
        }
        lastDrawnMap = signalMap
    }
}
