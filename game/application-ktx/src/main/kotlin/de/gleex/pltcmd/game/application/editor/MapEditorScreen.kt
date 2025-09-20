package de.gleex.pltcmd.game.application.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import de.gleex.pltcmd.game.application.editor.actors.HeightRenderActor
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import ktx.app.KtxScreen
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

class MapEditorScreen: KtxScreen {

    private val editableWorld = MutableWorld(worldSizeWidthInTiles = 300, worldSizeHeightInTiles = 300)

    private val camera = OrthographicCamera(500f,500f)
    private val viewport = ExtendViewport(5000f, 5000f, camera)
    private val stage = Stage(viewport)

    override fun show() {
        log.info { "Setting input processor" }
        Gdx.input.inputProcessor = stage

        log.info { "Creating coordinate rectangle" }
        val coordinateRectangleSequence = editableWorld.bottomLeftCoordinate..editableWorld.bottomLeftCoordinate.movedBy(10,10)
        log.info { "Starting to add actors" }
        coordinateRectangleSequence.forEach { coordinate ->
            log.info { "$coordinate | Adding height render actor" }
            stage.addActor(HeightRenderActor(coordinate, editableWorld))
        }
        camera.update()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
