package de.gleex.pltcmd.game.application.screens

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import de.gleex.pltcmd.model.world.WorldMap
import ktx.app.KtxScreen


/**
 * This screen is the main interface for the player. It mainly renders the given [WorldMap].
 */
class MainGameScreen(worldMap: WorldMap): KtxScreen {
    private val tiledMap: TiledMap = TiledMap()
    private var camera = OrthographicCamera()
    private val renderer = OrthoCachedTiledMapRenderer(tiledMap, (1f/16f))

    private val textureAtlasTerrain =
        TextureAtlas(Gdx.files.getFileHandle("terrain/packed/terrain.atlas", Files.FileType.Classpath))

    override fun show() {
        // set up camera
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)
        camera.update()

        // set up tiledMap
        val layer =TiledMapTileLayer(50, 50, 16, 16).apply {
            setCell(1, 1, TiledMapTileLayer.Cell().apply { tile = StaticTiledMapTile(textureAtlasTerrain.findRegion("grasslands")) })
        }
        tiledMap.layers.add(layer)

        // set up renderer
        renderer.setView(camera);

        // render???
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        renderer.render();
    }
}
