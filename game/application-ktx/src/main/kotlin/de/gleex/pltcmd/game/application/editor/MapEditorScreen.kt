package de.gleex.pltcmd.game.application.editor

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen

class MapEditorScreen: KtxScreen {

    private val camera = OrthographicCamera()
    private val stage = Stage(ExtendViewport(1000f, 800f, camera))
}
