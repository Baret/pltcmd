package de.gleex.pltcmd.game.application

import de.gleex.pltcmd.game.application.screens.MainGameScreen
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.WorldMap
import ktx.app.KtxGame
import ktx.app.KtxScreen
import mu.KotlinLogging
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {  }

class Pltcmd : KtxGame<KtxScreen>() {
    override fun create() {
//        addScreen(FirstScreen())
        val worldMap: WorldMap = generateWorld()
        val screen = MainGameScreen(worldMap)
        log.info { "Adding $screen" }
        addScreen(screen)
        log.info { "Loading screen" }
        setScreen<MainGameScreen>()
    }

    private fun generateWorld(): WorldMap {
        val seed = System.currentTimeMillis()
        log.info { "Generating world with seed $seed" }
        val worldMapGenerator = WorldMapGenerator(seed, 100, 100)
        val (worldMap, duration) = measureTimedValue { worldMapGenerator.generateWorld() }
        log.info { "Generated world in ${duration.toString(DurationUnit.SECONDS, decimals = 3)}" }
        return worldMap
    }
}

