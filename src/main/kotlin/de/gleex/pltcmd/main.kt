package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.PreviewGenerationListener
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.mapgenerators.ProgressListener
import de.gleex.pltcmd.model.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.GameOptions
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import de.gleex.pltcmd.ui.GeneratingView
import de.gleex.pltcmd.ui.TitleView
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen
import java.lang.Thread.sleep

fun main() {

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()

    showTitle(screen, tileGrid)

    val gameWorld = generateMap(screen, tileGrid)
    screen.dock(GameView(gameWorld, tileGrid))

    // testing display of units
    val visibleBlocks = gameWorld.visibleBlocks.toList()
    repeat(20) {
        val randomPosition = visibleBlocks.random()
        randomPosition.second.setUnit(TileRepository.Elements.PLATOON_FRIENDLY)
    }
}

private fun showTitle(screen: Screen, tileGrid: TileGrid) {
    screen.dock(TitleView(tileGrid))
    sleep(1500)
}

private fun generateMap(screen: Screen, tileGrid: TileGrid): GameWorld {
    val origin = Coordinate(0, 0)
    val mapGenerator = WorldMapGenerator(GameOptions.DEBUG_MAP_SEED)

    val previewWorld = createPreview(origin)
    val generatingView = GeneratingView(previewWorld, tileGrid)
    screen.dock(generatingView)

    val progressListener = ProgressListener(mapGenerator.sizeInTiles, 100.0, generatingView.progressProperty) // 100.0 is the default of ProgressBar
    val previewListener = PreviewGenerationListener(mapGenerator.worldWidthInTiles, mapGenerator.worldHeightInTiles, previewWorld)
    val worldMap = mapGenerator.generateWorld(origin, progressListener, previewListener)
    return GameWorld(worldMap)
}

private fun createPreview(origin: Coordinate): GameWorld {
    val emptyMap = WorldMap(setOf(Sector.createEmpty(origin)))
    return GameWorld(emptyMap)
}