package de.gleex.pltcmd.game.application.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FillViewport
import com.kotcrab.vis.ui.VisUI
import de.gleex.pltcmd.game.application.actors.WorldMapRendererActor
import de.gleex.pltcmd.game.engine.attributes.memory.KnownWorld
import de.gleex.pltcmd.model.world.WorldMap
import ktx.app.KtxScreen
import mu.KotlinLogging


private val log = KotlinLogging.logger { }

/**
 * This screen is the main interface for the player. It mainly renders the given [WorldMap].
 */
class MainGameScreen(private val knownWorld: KnownWorld) : KtxScreen {
    private val stage = Stage(FillViewport(1100f, 1000f))
    override fun show() {
        Gdx.input.inputProcessor = stage
        // set up actors
        stage.addActor(StupidClickableActor().apply {
            x = 0f
            y = 0f
            width = 300f
            height = 800f
        })
        stage.addActor(StupidClickableActor().apply {
            x = 0f
            y = 800f
            width = 1100f
            height = 200f
        })
        stage.addActor(WorldMapRendererActor(knownWorld).apply {
            x = 300f
            y = 0f
            width = 800f
            height = 800f
        }.also { stage.keyboardFocus = it })

        // FPS display
        stage.addActor(object : Label("${Gdx.graphics.framesPerSecond}", VisUI.getSkin()) {
            override fun act(delta: Float) {
                this.setText(Gdx.graphics.framesPerSecond)
                super.act(delta)
            }
        })
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}

private class StupidClickableActor : Actor() {

    private val renderer = ShapeRenderer()

    private val color1: Color = Color.FOREST
    private val color2: Color = Color.CYAN
    private var isColor1 = true

    private val currentColor: Color
        get() {
            return if(isColor1) {
                color1
            } else {
                color2
            }
        }

    init {
        this.color = currentColor
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isColor1 = !isColor1
                val newColor = currentColor
                addAction(Actions.color(newColor, 1f))
                return true
            }
        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()

        renderer.transformMatrix = batch?.transformMatrix
        renderer.projectionMatrix = batch?.projectionMatrix
        renderer.translate(x, y, 0f)

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = if(hasActions() && actions[0]!! is ColorAction) {
            (actions[0] as ColorAction).color
        } else {
            color
        }
        renderer.rect(0f, 0f, width, height)
        renderer.end()

        batch?.begin()
    }
}