package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.options.GameOptions
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
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

    val worldMap = WorldMapGenerator(GameOptions.DEBUG_MAP_SEED).generateWorld()
    val gameWorld = GameWorld(worldMap)
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
    TimeUnit.MILLISECONDS.sleep(1500)
}