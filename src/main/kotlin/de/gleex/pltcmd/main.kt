package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen

fun main() {
    val initialOrigin = Coordinate(350, 200)
    val worldMap = WorldMap(
            setOf(
                    Sector.generateAt(initialOrigin)))

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val gameWorld = GameWorld(worldMap)
    gameWorld.scrollToCoordinate(initialOrigin)
    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen().also { it.dock(GameView(gameWorld, tileGrid)) }

    // testing display of units
    val visibleBlocks = gameWorld.fetchBlocksAt(gameWorld.visibleOffset, gameWorld.visibleSize).toList()
    repeat(20) {
        val randomPosition = visibleBlocks.random()
        randomPosition.second.setUnit(TileRepository.PLATOON_FRIENDLY)
    }
}