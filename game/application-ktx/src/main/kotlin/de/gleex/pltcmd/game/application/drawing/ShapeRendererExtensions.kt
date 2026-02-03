package de.gleex.pltcmd.game.application.drawing

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 * Begins and ends drawing on this [ShapeRenderer].
 *
 * @see ShapeRenderer.begin
 * @see ShapeRenderer.end
 */
internal inline fun ShapeRenderer.drawWithType(
    type: ShapeRenderer.ShapeType,
    crossinline drawInstructions: ShapeRenderer.() -> Unit
) {
    begin(type)
    drawInstructions()
    end()
}