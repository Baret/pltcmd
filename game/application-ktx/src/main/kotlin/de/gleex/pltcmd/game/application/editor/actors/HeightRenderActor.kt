package de.gleex.pltcmd.game.application.editor.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.scenes.scene2d.Actor
import de.gleex.pltcmd.game.application.drawing.drawWithType
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.distance.DistanceUnit

class   HeightRenderActor(private val coordinate: Coordinate, private val world: MutableWorld) : Actor() {

    private val renderer = ShapeRenderer()
    private val edgeLength = WorldTile.edgeLength.inUnit(DistanceUnit.Meters).toFloat()

    init {
        val x = (coordinate.eastingFromLeft - world.bottomLeftCoordinate.eastingFromLeft) * edgeLength
        val y = (coordinate.northingFromBottom - world.bottomLeftCoordinate.northingFromBottom) * edgeLength
        setPosition(x, y)
        width = edgeLength
        height = edgeLength
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()

        renderer.transformMatrix = batch?.transformMatrix
        renderer.projectionMatrix = batch?.projectionMatrix
        renderer.drawWithType(Filled) {
            val color = if(coordinate.eastingFromLeft % 2 == 0) {
                if(coordinate.northingFromBottom % 2 == 0) {
                    Color.BLACK
                } else {
                    Color.LIGHT_GRAY
                }
            } else {
                if(coordinate.northingFromBottom % 2 == 0) {
                    Color.LIGHT_GRAY
                } else {
                    Color.BLACK
                }
            }
            rect(x, y, edgeLength, edgeLength)
        }

        batch?.begin()
    }
}
