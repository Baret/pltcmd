package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.mapgenerators.ProgressListener
import de.gleex.pltcmd.model.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.mapgenerators.ui.PreviewGenerationListener
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import de.gleex.pltcmd.ui.GeneratingView
import de.gleex.pltcmd.ui.TitleView
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen
import java.util.concurrent.TimeUnit

fun main() {

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()

    showTitle(screen, tileGrid)

    generateMap(screen, tileGrid) { generatedMap ->
        val gameWorld = GameWorld(generatedMap)
        screen.dock(GameView(gameWorld, tileGrid))

        // testing display of units
        val visibleBlocks = gameWorld.visibleBlocks.toList()
        repeat(20) {
            val randomPosition = visibleBlocks.random()
            randomPosition.second.setUnit(TileRepository.Elements.PLATOON_FRIENDLY)
        }
    }
}

private fun showTitle(screen: Screen, tileGrid: TileGrid) {
    screen.dock(TitleView(tileGrid))
    TimeUnit.MILLISECONDS.sleep(1500)
}

private fun generateMap(screen: Screen, tileGrid: TileGrid, doneCallback: (WorldMap) -> Unit) {
    val generatingView = GeneratingView(tileGrid)
    screen.dock(generatingView)

    val mapGenerator = WorldMapGenerator()
    val progressListener = ProgressListener(mapGenerator.sizeInTiles, 100.0, generatingView.progressProperty) // 100.0 is the default of ProgressBar
    val previewListener = PreviewGenerationListener(mapGenerator.worldWidthInTiles, mapGenerator.worldHeightInTiles, generatingView.incompleteWorld)
    val origin = Coordinate(0, 0)
    val worldMap = mapGenerator.generateWorld(origin, progressListener, previewListener)

    generatingView.onConfirmation {
        doneCallback(worldMap)
    }
}
