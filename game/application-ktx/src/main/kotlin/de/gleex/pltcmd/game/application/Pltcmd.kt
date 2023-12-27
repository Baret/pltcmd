package de.gleex.pltcmd.game.application

import com.kotcrab.vis.ui.VisUI
import de.gleex.pltcmd.game.application.screens.FirstScreen
import de.gleex.pltcmd.game.application.screens.MainGameScreen
import de.gleex.pltcmd.game.engine.attributes.memory.KnownWorld
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
        log.info { "loading VisUI" }
        VisUI.load(VisUI.SkinScale.X1)

        addScreen(FirstScreen())
        val worldMap: WorldMap = generateWorld()
        val screen = MainGameScreen(KnownWorld(worldMap))
        log.info { "Adding $screen" }
        addScreen(screen)
        log.info { "Loading screen" }
        setScreen<MainGameScreen>()
//        setScreen<FirstScreen>()
    }

    private fun generateWorld(): WorldMap {
        val seed: Long = 23 //System.currentTimeMillis()
        log.info { "Generating world with seed $seed" }
        val worldMapGenerator = WorldMapGenerator(seed, 100, 100)
        val (worldMap, duration) = measureTimedValue { worldMapGenerator.generateWorld() }
        log.info { "Generated world in ${duration.toString(DurationUnit.SECONDS, decimals = 3)}" }
        return worldMap
    }
}

