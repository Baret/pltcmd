package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.GameView
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.data.impl.Position3D


fun main() {
    val worldMap = WorldMap(
            setOf(
                    Sector.generateAt(
                            Coordinate(350, 200))))

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val gameWorld = GameWorld(worldMap)
    gameWorld.scrollTo3DPosition(Position3D.create(350, 200,0))
    application.dock(GameView(gameWorld))
}