package de.gleex.pltcmd.game.application.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import de.gleex.pltcmd.game.application.editor.actors.HeightRenderActor
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import ktx.app.KtxScreen
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class MapEditorScreen : KtxScreen {

    private val editableWorld = MutableWorld(worldSizeWidthInTiles = 300, worldSizeHeightInTiles = 300)

    private lateinit var stage: Stage

    override fun show() {
        log.info { "Creating camera, viewport and stage" }
        // camera = screen size
        val camera: Camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val sectorEdgeLength = (Sector.TILE_COUNT * WorldTile.edgeLength.inUnit(DistanceUnit.Meters)).toFloat()
        // viewport = world size
        log.info { "Viewport world size = $sectorEdgeLength" }
        val viewport: Viewport = ExtendViewport(sectorEdgeLength, sectorEdgeLength, camera)

        stage = Stage(viewport)

        log.info { "Setting input processor" }
        Gdx.input.inputProcessor = stage

        log.info { "Creating coordinate rectangle" }
        val coordinateRectangleSequence =
            editableWorld.bottomLeftCoordinate..editableWorld.bottomLeftCoordinate.movedBy(10, 10)
        log.info { "Starting to add actors" }
        coordinateRectangleSequence.forEach { coordinate ->
            log.info { "$coordinate | Adding height render actor" }
            stage.addActor(HeightRenderActor(coordinate, editableWorld))
        }
        camera.update()
    }

    override fun render(delta: Float) {
        stage.batch.projectionMatrix = stage.camera.combined
        stage.camera.update()
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
        stage.camera.viewportWidth = Gdx.graphics.width.toFloat()
        stage.camera.viewportHeight = Gdx.graphics.height.toFloat()
        stage.camera.position.set(stage.camera.viewportWidth / 2f, stage.camera.viewportHeight / 2f, 0f)
        stage.camera.update()
    }
}
