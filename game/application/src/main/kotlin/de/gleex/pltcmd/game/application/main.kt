package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.GameView
import de.gleex.pltcmd.game.ui.GeneratingView
import de.gleex.pltcmd.game.ui.MapGenerationProgressController
import de.gleex.pltcmd.game.ui.TitleView
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
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
    TimeUnit.MILLISECONDS.sleep(4000)
}

private fun generateMap(screen: Screen, tileGrid: TileGrid, doneCallback: (WorldMap) -> Unit) {
    val generatingView = GeneratingView(tileGrid)
    screen.dock(generatingView)

    val mapGenerator = WorldMapGenerator(
            GameOptions.DEBUG_MAP_SEED,
            GameOptions.SECTORS_COUNT_H * Sector.TILE_COUNT,
            GameOptions.SECTORS_COUNT_V * Sector.TILE_COUNT
    )
    MapGenerationProgressController(mapGenerator, generatingView)

    val origin = Coordinate(0, 0)
    val worldMap = mapGenerator.generateWorld(origin)

    generatingView.onConfirmation {
        doneCallback(worldMap)
    }
}
