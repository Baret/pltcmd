package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.signals.core.Signal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Draws a [Signal] at its origin when activated. When deactivated the last drawn overlays are cleared.
 */
class SignalVisualizer(val world: GameWorld) {

    companion object {
        private val log = LoggerFactory.getLogger(SignalVisualizer::class)
    }

    private var currentJob: Job? = null
    private var currentBlocks: MutableSet<GameBlock> = mutableSetOf()

    /**
     * Clears the drawn overlays so that no signal is visible anymore.
     */
    fun deactivate() {
        currentJob?.let {
            log.debug("Cancelling job")
            it.cancel()
        }
        clear()
        currentJob = null
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
        log.debug("Displaying signal at ${signal.origin}")
        deactivate()
        draw(signal)
    }

    private fun draw(signal: Signal<*>) {
        currentJob = signal.all
                .onEach { (coordinate, signalStrength) ->
                    world.fetchBlockAt(coordinate)
                            .ifPresent { block ->
                                block.setOverlay(TileRepository.forSignal(signalStrength))
                                currentBlocks.add(block)
                            }
                }
                .launchIn(CoroutineScope(Dispatchers.Default))
    }
}
