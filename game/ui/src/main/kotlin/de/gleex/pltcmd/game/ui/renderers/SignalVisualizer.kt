package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.signals.core.Signal

/**
 * Draws a [Signal] at its origin when activated. When deactivated the last drawn overlays are cleared.
 */
class SignalVisualizer(val world: GameWorld) {

    private var currentBlocks: MutableSet<GameBlock> = mutableSetOf()

    /**
     * Clears the drawn overlays so that no signal is visible anymore.
     */
    fun deactivate() {
        clear()
    }

    private fun clear() {
        currentBlocks.forEach { block ->
            block.resetOverlay()
        }
        currentBlocks.clear()
    }

    /**
     * Draws the given signal at its origin.
     */
    fun activateWith(signal: Signal<*>) {
        deactivate()
        draw(signal)
    }

    private fun draw(signal: Signal<*>) {
        signal.signalMap.forEach { (coordinate, signalStrength) ->
            world.fetchBlockAt(coordinate)
                    .ifPresent { block ->
                        block.setOverlay(TileRepository.forSignal(signalStrength))
                        currentBlocks.add(block)
                    }
        }
    }
}
