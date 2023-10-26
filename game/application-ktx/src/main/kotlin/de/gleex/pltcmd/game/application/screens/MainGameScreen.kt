package de.gleex.pltcmd.game.application.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import de.gleex.pltcmd.model.world.WorldMap
import ktx.app.KtxScreen

/**
 * This screen is the main interface for the player. It mainly renders the given [WorldMap].
 */
class MainGameScreen(worldMap: WorldMap): KtxScreen {
    private val tiledMap: TiledMap = TiledMap()
    private val camera = OrthographicCamera()
    private val renderer = OrthoCachedTiledMapRenderer(tiledMap, (1f/16f))

    override fun show() {
        // set up tiledMap

        // set up renderer

        // render???
    }
}
