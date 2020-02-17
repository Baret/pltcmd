package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.options.GameOptions
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen

fun main() {

    val worldMap = WorldMapGenerator(GameOptions.DEBUG_MAP_SEED).generateWorld()

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val gameWorld = GameWorld(worldMap)
    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()
    screen.dock(GameView(gameWorld, tileGrid))

    // testing display of units
    val visibleBlocks = gameWorld.visibleBlocks.toList()
    repeat(20) {
        val randomPosition = visibleBlocks.random()
        //randomPosition.second.setUnit(TileRepository.Elements.PLATOON_FRIENDLY)
    }
}