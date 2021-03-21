package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.game.ui.views.GeneratingView
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.ProgressListener
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import org.hexworks.cobalt.events.api.Subscription
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Updates the given view while the given map generator creates a world. */
class MapGenerationProgressController(private val mapGenerator: WorldMapGenerator, private val view: GeneratingView) {

    private val scheduledUpdates = Executors.newSingleThreadScheduledExecutor()

    private val progressListener = ProgressListener(mapGenerator.sizeInTiles)
    private val progress = progressListener.progress
    private val previewListener = PreviewGenerationListener(mapGenerator.worldWidthInTiles, mapGenerator.worldHeightInTiles, view.incompleteWorld)
    private val progressFinishedListener: Subscription

    init {
        // update progress bar only every now and then because it lasts some time
        scheduledUpdates.scheduleAtFixedRate({
            view.setProgressBarTo(progress.value)
        }, 100L, 100L, TimeUnit.MILLISECONDS)
        progressFinishedListener = progress.onChange { if (it.newValue == 1.0) onFinished() }

        registerListeners()
    }

    private fun registerListeners() {
        with(mapGenerator) {
            addListener(progressListener)
            addListener(previewListener)
        }
    }

    private fun unregisterListeners() {
        progressFinishedListener?.dispose()
        with(mapGenerator) {
            removeListener(progressListener)
            removeListener(previewListener)
        }
    }

    private fun onFinished() {
        scheduledUpdates.shutdown()
        unregisterListeners()
        view.showFinished()
    }

}
