package de.gleex.pltcmd.game.application.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import de.gleex.pltcmd.game.application.editor.actions.MoveCameraActor
import de.gleex.pltcmd.game.application.editor.actors.HeightRenderActor
import de.gleex.pltcmd.game.application.editor.actors.LogIWasRendered
import de.gleex.pltcmd.game.application.editor.listeners.CameraZoomListener
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import ktx.app.KtxScreen
import mu.KotlinLogging
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val log = KotlinLogging.logger { }

class MapEditorScreen : KtxScreen {

    private val editableWorld = MutableWorld(worldSizeWidthInTiles = worldWidthInTiles, worldSizeHeightInTiles = worldHeightInTiles)

    private lateinit var stage: Stage

    override fun show() {
        log.info { "Creating camera, viewport and stage" }
        // camera = screen size
        val camera: OrthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        val sectorEdgeLength = (Sector.TILE_COUNT * WorldTile.edgeLength.inUnit(DistanceUnit.Meters)).toFloat()
        // viewport = world size
        log.info { "Viewport world size = $sectorEdgeLength" }
        val viewport: Viewport = ExtendViewport(sectorEdgeLength, sectorEdgeLength, camera)

        stage = Stage(viewport)

        log.info { "Setting input processor" }
        Gdx.input.inputProcessor = stage
        stage.registerListeners()

        log.info { "Creating coordinate rectangle" }
        val coordinateRectangleSequence =
            editableWorld.bottomLeftCoordinate..editableWorld.bottomLeftCoordinate.movedBy(10, 10)
        log.info { "Starting to add actors" }
        coordinateRectangleSequence
            .forEach { coordinate ->
                log.info { "$coordinate | Adding height render actor" }
                stage.addActor(HeightRenderActor(coordinate, editableWorld))
            }
        stage.addActor(LogIWasRendered)
        camera.update()
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun render(delta: Float) {
        stage.batch.projectionMatrix = stage.camera.combined
        stage.camera.update()
        stage.act(delta)
        stage.draw()
        LogIWasRendered.logNow.store(false)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
        stage.camera.viewportWidth = Gdx.graphics.width.toFloat()
        stage.camera.viewportHeight = Gdx.graphics.height.toFloat()
        stage.camera.position.set(stage.camera.viewportWidth / 2f, stage.camera.viewportHeight / 2f, 0f)
        stage.camera.update()
    }

    private fun Stage.registerListeners() {
//        addListener(LogIWasRenderedListener())
        val currentCamera = camera
        if(currentCamera is OrthographicCamera) {
            addListener(CameraZoomListener(currentCamera))
            addActor(MoveCameraActor(currentCamera))
        }
    }
}
