package de.gleex.pltcmd.game.application.actors

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

class WorldMapRendererActor(private val worldMap: WorldMap) : Actor() {
    private val renderer = ShapeRenderer()

    private var bottomLeftCoordinate = worldMap.origin

    init {
        color = Color.DARK_GRAY
        addListener(object: InputListener() {
            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                log.info { "Received keyDown event $event keyCode = $keycode" }
                return when (keycode) {
                    Input.Keys.A -> {
                        scrollByCoordinates(-1, 0)
                        true
                    }
                    Input.Keys.D -> {
                        scrollByCoordinates(1, 0)
                        true
                    }
                    Input.Keys.W -> {
                        scrollByCoordinates(0, 1)
                        true
                    }
                    Input.Keys.S -> {
                        scrollByCoordinates(0, -1)
                        true
                    }
                    else         -> {
                        false
                    }
                }
            }
        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()

        renderer.transformMatrix = batch?.transformMatrix
        renderer.projectionMatrix = batch?.projectionMatrix
        renderer.translate(x, y, 0f)

        renderer.fillBackground()

        renderer.drawTiles()

        renderer.drawGridLines()

        batch?.begin()
    }

    private fun ShapeRenderer.drawTiles() {
        forEachVisibleCoordinate { currentCoordinate ->
            if (currentCoordinate in worldMap) {
                drawTile(worldMap[currentCoordinate])
            }
        }
    }

    private fun forEachVisibleCoordinate(action: (Coordinate) -> Unit) {
        var currentCoordinate = bottomLeftCoordinate
        var currentDrawPosition: Vector2
        do {
            action(currentCoordinate)
            currentCoordinate = currentCoordinate.movedBy(1, 0)
            currentDrawPosition = drawPositionOf(currentCoordinate)
            if (currentDrawPosition.x >= width) {
                currentCoordinate = currentCoordinate.withEasting(bottomLeftCoordinate.eastingFromLeft).movedBy(0, 1)
                currentDrawPosition = drawPositionOf(currentCoordinate)
            }
        } while (currentDrawPosition.x < width && currentDrawPosition.y < height)
    }

    private fun ShapeRenderer.drawTile(worldTile: WorldTile) {
        val drawPos = drawPositionOf(worldTile.coordinate)
        drawWithType(ShapeRenderer.ShapeType.Filled) {
            // TODO: draw height
            color = terrainTypeColor[worldTile.type]
            rect(drawPos.x, drawPos.y, TILE_WIDTH, TILE_HEIGHT)
        }
    }

    private fun ShapeRenderer.fillBackground() {
        drawWithType(ShapeRenderer.ShapeType.Filled) {
            color = this@WorldMapRendererActor.color
            rect(0f, 0f, width, height)
        }
    }

    private fun ShapeRenderer.drawGridLines() {
        forEachVisibleCoordinate { currentCoordinate ->
            val drawPos = drawPositionOf(currentCoordinate)
            drawWithType(ShapeRenderer.ShapeType.Line) {
                color = if(currentCoordinate.northingFromBottom == currentCoordinate.sectorOrigin.northingFromBottom) {
                    Color.RED
                } else {
                    Color.BLACK
                }
                val bottomRight = Vector2(drawPos).add(TILE_WIDTH, 0f)
                line(drawPos, bottomRight)
                color = if(currentCoordinate.eastingFromLeft == currentCoordinate.sectorOrigin.eastingFromLeft) {
                    Color.RED
                } else {
                    Color.BLACK
                }
                val topLeft = Vector2(drawPos).add(0f, TILE_HEIGHT)
                line(drawPos, topLeft)
            }
        }
    }

    private inline fun ShapeRenderer.drawWithType(
        type: ShapeRenderer.ShapeType,
        crossinline drawInstructions: ShapeRenderer.() -> Unit
    ) {
        begin(type)
        drawInstructions()
        end()
    }

    /**
     * Translates the given coordinate to the position inside of this actor.
     */
    private fun drawPositionOf(coordinate: Coordinate): Vector2 {
        val currentOriginCoordinate = coordinate - bottomLeftCoordinate
        val drawPosX: Float = currentOriginCoordinate.eastingFromLeft.toFloat() * TILE_WIDTH
        val drawPosY: Float = currentOriginCoordinate.northingFromBottom.toFloat() * TILE_HEIGHT
        return Vector2(drawPosX, drawPosY)
    }

    private fun scrollByCoordinates(scrollAmountEasting: Int, scrollAmountNorthing: Int) {
        val newCoordinate = bottomLeftCoordinate.movedBy(scrollAmountEasting, scrollAmountNorthing)
        if (newCoordinate in worldMap) {
            bottomLeftCoordinate = newCoordinate
        }
    }

    companion object {
        private val terrainTypeColor: Map<TerrainType, Color> = TerrainType.entries.toTypedArray().associate {
            when (it) {
                TerrainType.GRASSLAND     -> it to Color.LIGHT_GRAY
                TerrainType.FOREST        -> it to Color.OLIVE
                TerrainType.HILL          -> it to Color.GRAY
                TerrainType.MOUNTAIN      -> it to Color.BROWN
                TerrainType.WATER_DEEP    -> it to Color.BLUE
                TerrainType.WATER_SHALLOW -> it to Color.CYAN
            }
        }

        private const val TILE_WIDTH = 16f
        private const val TILE_HEIGHT = 16f
    }
}