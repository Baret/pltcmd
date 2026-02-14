package de.gleex.pltcmd.game.application.actors.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import de.gleex.pltcmd.model.world.terrain.TerrainHeight

/**
 * This [Actor] should be used as overlay over a [TerrainTypeActor]. It draws a transparent rectangle, tinting
 * its underlying graphics.
 */
class TerrainHeightActor(terrainHeight: TerrainHeight) : Actor() {
    private val tintFactor = when (terrainHeight) {
        TerrainHeight.ONE   -> 0.0f
        TerrainHeight.TWO   -> 0.1f
        TerrainHeight.THREE -> 0.2f
        TerrainHeight.FOUR  -> 0.3f
        TerrainHeight.FIVE  -> 0.4f
        TerrainHeight.SIX   -> 0.5f
        TerrainHeight.SEVEN -> 0.6f
        TerrainHeight.EIGHT -> 0.7f
        TerrainHeight.NINE  -> 0.8f
        TerrainHeight.TEN   -> 0.9f
    }

    private val shapeColor by lazy {
        val greyScale = 255f * tintFactor
        Color(greyScale, greyScale, greyScale, 0.5f)
    }

    private val renderer = ShapeRenderer()

    init {
        setSize(1f, 1f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()

        // TODO: maybe there is a better way to enable blending, which makes the underlying image visible when alpha < 1
        Gdx.gl.glEnable(GL20.GL_BLEND)

        with(renderer) {
            setProjectionMatrix(batch?.projectionMatrix)
            setTransformMatrix(batch?.transformMatrix)
            translate(x, y, 0f)

            begin(ShapeRenderer.ShapeType.Filled)
            color = this@TerrainHeightActor.shapeColor
            rect(0f, 0f, width, height)
            end()

        }

        Gdx.gl.glDisable(GL20.GL_BLEND)

        batch?.begin()
    }
}
