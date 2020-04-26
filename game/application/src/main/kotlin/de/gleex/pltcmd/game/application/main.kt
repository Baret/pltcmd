package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.engine.entities.ElementEntity
import de.gleex.pltcmd.game.engine.facets.MoveTo
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.GameView
import de.gleex.pltcmd.game.ui.GeneratingView
import de.gleex.pltcmd.game.ui.MapGenerationProgressController
import de.gleex.pltcmd.game.ui.TitleView
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.runBlocking
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen
import stress.TestContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {

    val application = SwingApplications.startApplication(UiOptions.buildAppConfig())
    val tileGrid = application.tileGrid
    val screen = tileGrid.toScreen()

    showTitle(screen, tileGrid)

    generateMap(screen, tileGrid) { generatedMap ->
        val gameWorld = GameWorld(generatedMap)
        screen.dock(GameView(gameWorld, tileGrid))

        // testing display of units
        val mover = Executors.newScheduledThreadPool(1)
        val c = TestContext
        repeat(20) {
            val randomPosition = generatedMap.sectors.find { it.origin == Coordinate(0, 450) }!!.tiles.random().coordinate
            val element = ElementEntity(Element(CallSign("Element $it"), emptySet()), randomPosition)
            gameWorld.trackUnit(element)
            val newPos = randomPosition.movedBy(if (Random.nextBoolean()) 1 else -1, if (Random.nextBoolean()) 1 else -1)
            val moveTo = MoveTo(newPos, c, element)
            mover.schedule({
                runBlocking {
                    element.executeCommand(moveTo)
                    element.update(moveTo.context)
                }
            }, Random.nextLong(5), TimeUnit.SECONDS)
        }
        mover.shutdown()
    }
}

private fun showTitle(screen: Screen, tileGrid: TileGrid) {
    screen.dock(TitleView(tileGrid))
    TimeUnit.MILLISECONDS.sleep(1500)
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
