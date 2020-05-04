package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.ElementType
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.GameView
import de.gleex.pltcmd.game.ui.GeneratingView
import de.gleex.pltcmd.game.ui.MapGenerationProgressController
import de.gleex.pltcmd.game.ui.TitleView
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Engine
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())

    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()

    showTitle(screen, tileGrid)

    generateMap(screen, tileGrid) { generatedMap ->
        val gameWorld = GameWorld(generatedMap)
        val game = Game(Engine.default(), generatedMap, Random(GameOptions.DEBUG_MAP_SEED))

        val elementsToCommand = mutableListOf<GameEntity<ElementType>>()
        val visibleSector = generatedMap.sectors.first { it.origin == Coordinate(0, 450) }
        game.addElementInSector(visibleSector, "Alpha", Affiliation.Friendly)?.let { elementsToCommand.add(it); gameWorld.trackUnit(it) }
        game.addElementInSector(visibleSector, "Bravo", Affiliation.Friendly)?.let { elementsToCommand.add(it); gameWorld.trackUnit(it) }
        game.addElementInSector(visibleSector, "Charlie", Affiliation.Friendly)?.let { elementsToCommand.add(it); gameWorld.trackUnit(it) }
        screen.dock(GameView(gameWorld, tileGrid, elementsToCommand))

        // Adding some elements to every sector
        val elementsPerSector = 3
        generatedMap.sectors.forEach { sector ->
            repeat(elementsPerSector) {
                game.addElementInSector(sector)?.
                    let {
                        gameWorld.trackUnit(it)
                    }
            }
        }
        Ticker.start(game)
        // cleanup
        screen.onShutdown { Ticker.stopGame() }
    }
}

private fun showTitle(screen: Screen, tileGrid: TileGrid) {
    if (UiOptions.SKIP_INTRO.not()) {
        screen.dock(TitleView(tileGrid))
        TimeUnit.MILLISECONDS.sleep(4000)
    }
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
