package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.mapgeneration.MapGenerationProgressController
import de.gleex.pltcmd.game.ui.views.GameView
import de.gleex.pltcmd.game.ui.views.GeneratingView
import de.gleex.pltcmd.game.ui.views.TitleView
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.toSectorOrigin
import org.hexworks.amethyst.api.Engine
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.extensions.toScreen
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val log = LoggerFactory.getLogger(::main::class)
private val random = Random(GameOptions.MAP_SEED)

fun main() {
    Main().run()
}

/**
 * Setups, starts and runs the game.
 */
open class Main {

    /**
     * Creates a window and a map and runs the game.
     */
    open fun run() {
        val application = SwingApplications.startApplication(UiOptions.buildAppConfig())

        val tileGrid = application.tileGrid
        val screen = tileGrid.toScreen()

        showTitle(screen, tileGrid)

        generateMap(screen, tileGrid) { generatedMap ->
            runGame(generatedMap, screen, tileGrid)
        }
    }

    /**
     * Creates a [Game] with the given map. Initializes that game and creates a [GameView] on the given [Screen].
     * Then the [Ticker] runs the game.
     */
    protected open fun runGame(generatedMap: WorldMap, screen: Screen, tileGrid: TileGrid) {
        // model
        val playerFaction = Faction("player faction")
        val game = Game(Engine.create(), generatedMap, playerFaction, random)
        // ui
        val gameWorld = GameWorld(generatedMap, playerFaction)

        val (elementsToCommand, hq) = prepareGame(game, gameWorld)

        screen.dock(GameView(gameWorld, tileGrid, game, hq, elementsToCommand))

        Ticker.start()
        // cleanup
        screen.onShutdown { Ticker.stop() }
    }

    /**
     * The plain world can be extended or some game state can be set prior to running the [Game].
     * This implementation adds some elements in the visible [Sector] including a headquarter. Hostiles will be placed
     * all over the map.
     *
     * @return the elements to command in the UI and the HQ entity for sending messages from the UI.
     */
    protected open fun prepareGame(game: Game, gameWorld: GameWorld): Pair<List<ElementEntity>, FOBEntity> {
        val visibleSector = game.world.sectors.first {
            it.origin == gameWorld.visibleTopLeftCoordinate().toSectorOrigin()
        }
        val elementsToCommand = createElementsToCommand(visibleSector, game, gameWorld)
        val hq = game.newHQIn(visibleSector, game.playerFaction)
            .also { gameWorld.showBase(it) }
        addHostiles(game, gameWorld)
        return Pair(elementsToCommand, hq)
    }

    /**
     * @return the elements that should be controllable by the UI
     */
    protected open fun createElementsToCommand(visibleSector: Sector, game: Game, gameWorld: GameWorld): List<ElementEntity> {
        val faction = game.playerFaction
        val elementsToCommand = mutableListOf<ElementEntity>()
        elementsToCommand.run {

            val alpha = visibleSector.createFriendly(Elements.transportHelicopterPlatoon.new()
                    .apply { callSign = CallSign("Alpha") }, faction, game, gameWorld)
            val bravo = visibleSector.createFriendly(Elements.riflePlatoon.new()
                    .apply { callSign = CallSign("Bravo") }, faction, game, gameWorld)
            val charlie = visibleSector.createFriendly(Elements.reconPlane.new()
                    .apply { callSign = CallSign("Charlie") }, faction, game, gameWorld)
            listOf(alpha, bravo, charlie).forEach {
                log.debug("${it.callsign} is a ${it.element.description} with a speed of ${it.baseSpeedInKph} kph.")
            }
            add(alpha)
            add(bravo)
            add(charlie)
        }
        return elementsToCommand
    }

    /**
     * Add elements to the game that are not controlled by the player. This implementation adds 2 rifle squads per [Sector].
     */
    protected open fun addHostiles(game: Game, gameWorld: GameWorld) {
        val opfor = Faction("opposing force")
        opfor.relations[game.playerFaction] = Affiliation.Hostile
        game.playerFaction.relations[opfor] = Affiliation.Hostile
        // Adding some elements to every sector
        val elementsPerSector = 2
        game.world.sectors.forEach { sector ->
            repeat(elementsPerSector) {
                game.addElementInSector(sector, Elements.rifleSquad.new(), faction = opfor, playerControlled = false)
                        .also(gameWorld::trackUnit)
            }
        }
    }

    /**
     * Creates and adds an entity for the given element to the [Game]. It will be displayed on the [GameWorld].
     * The position is random by default and the [Affiliation] is friendly. For other affiliations a wandering entity
     * will be added.
     */
    protected fun Sector.createFriendly(element: CommandingElement, faction: Faction, game: Game, gameWorld: GameWorld, position: Coordinate = this.randomCoordinate(random)): ElementEntity {
        return game.addElementInSector(this, element, position, faction, true)
                .also(gameWorld::trackUnit)
    }

    /**
     * Displays the title screen for a short period of time if not skipped in the [UiOptions].
     * @see UiOptions.SKIP_INTRO
     */
    protected open fun showTitle(screen: Screen, tileGrid: TileGrid) {
        if (UiOptions.SKIP_INTRO.not()) {
            screen.dock(TitleView(tileGrid))
            TimeUnit.MILLISECONDS.sleep(4000)
        }
    }

    /**
     * Create the [WorldMap] for the game. This implementation uses a [WorldMapGenerator] and shows the progress on screen.
     * The created map must be provided to the given [doneCallback].
     */
    protected open fun generateMap(screen: Screen, tileGrid: TileGrid, doneCallback: (WorldMap) -> Unit) {
        val worldWidthInTiles = GameOptions.SECTORS_COUNT_H * Sector.TILE_COUNT
        val worldHeightInTiles = GameOptions.SECTORS_COUNT_V * Sector.TILE_COUNT

        val generatingView = GeneratingView(tileGrid, Size.create(worldWidthInTiles, worldHeightInTiles))
        screen.dock(generatingView)

        val mapGenerator = WorldMapGenerator(
                GameOptions.MAP_SEED,
                worldWidthInTiles,
                worldHeightInTiles
        )
        MapGenerationProgressController(mapGenerator, generatingView)

        val origin = GameOptions.MAP_ORIGIN
        val worldMap = mapGenerator.generateWorld(origin)

        generatingView.onConfirmation {
            doneCallback(worldMap)
        }
    }
}
