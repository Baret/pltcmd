package de.gleex.pltcmd.game.application.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.VisUI
import de.gleex.pltcmd.game.engine.attributes.memory.KnownTerrain
import de.gleex.pltcmd.game.engine.attributes.memory.KnownWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.fillCircle
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit.Meters
import mu.KotlinLogging
import kotlin.math.floor

private val log = KotlinLogging.logger { }

/**
 * Renders the given [KnownWorld] and offers basic controls for the user like scrolling.
 */
class WorldMapRendererActor(private val knownWorld: KnownWorld) : Group() {
    private val renderer = ShapeRenderer()

    private var bottomLeftCoordinate: Coordinate = knownWorld.origin.origin

    private val coordinateHighlightLabel = Label(bottomLeftCoordinate.toString(), VisUI.getSkin())

    init {
        color = Color.DARK_GRAY
        setupListeners()
        log.info { "My origin: $originX | $originY on stage ${localToStageCoordinates(Vector2(originX, originY))}" }
        addActor(coordinateHighlightLabel)
        coordinateHighlightLabel.isVisible = false
    }

    override fun act(delta: Float) {
        if (delta > 0f) {
            if (Gdx.input.isKeyPressed(Keys.A)) scrollByCoordinates(-1, 0)
            if (Gdx.input.isKeyPressed(Keys.D)) scrollByCoordinates(1, 0)
            if (Gdx.input.isKeyPressed(Keys.W)) scrollByCoordinates(0, 1)
            if (Gdx.input.isKeyPressed(Keys.S)) scrollByCoordinates(0, -1)
        }
        super.act(delta)
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
        coordinateHighlightLabel.draw(batch, parentAlpha)
    }

    private fun ShapeRenderer.drawTiles() {
        forEachVisibleCoordinate { currentCoordinate ->
            drawTile(knownWorld[currentCoordinate])
        }
    }

    private inline fun forEachVisibleCoordinate(action: (Coordinate) -> Unit) {
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

    private fun ShapeRenderer.drawTile(worldTile: KnownTerrain) {
        val drawPos = drawPositionOf(worldTile.coordinate)
        drawWithType(ShapeRenderer.ShapeType.Filled) {
            // TODO: draw height
            color = worldTile.terrain?.type?.let { terrainTypeColor[it] } ?: Color.BLACK
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
                color = if (currentCoordinate.northingFromBottom == currentCoordinate.sectorOrigin.northingFromBottom) {
                    Color.RED
                } else {
                    Color.BLACK
                }
                val bottomRight = Vector2(drawPos).add(TILE_WIDTH, 0f)
                line(drawPos, bottomRight)
                color = if (currentCoordinate.eastingFromLeft == currentCoordinate.sectorOrigin.eastingFromLeft) {
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

    /**
     * Translates a point in this actor to the underlying coordinate
     */
    private fun coordinateAtActorPosition(x: Float, y: Float): Coordinate {
        return bottomLeftCoordinate.movedBy(floor(x / TILE_WIDTH).toInt(), floor(y / TILE_HEIGHT).toInt())
    }

    private fun scrollByCoordinates(scrollAmountEasting: Int, scrollAmountNorthing: Int) {
        val newCoordinate = bottomLeftCoordinate.movedBy(scrollAmountEasting, scrollAmountNorthing)
        if (newCoordinate in knownWorld.origin) {
            bottomLeftCoordinate = newCoordinate
        }
    }

    private fun setupListeners() {
        val scrollListener = object : InputListener() {
            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                return when (keycode) {
                    Keys.A -> {
                        scrollByCoordinates(-1, 0)
                        true
                    }

                    Keys.D -> {
                        scrollByCoordinates(1, 0)
                        true
                    }

                    Keys.W -> {
                        scrollByCoordinates(0, 1)
                        true
                    }

                    Keys.S -> {
                        scrollByCoordinates(0, -1)
                        true
                    }

                    else   -> {
                        false
                    }
                }
            }
        }
        val revealListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return if (button == Buttons.LEFT) {
                    knownWorld.reveal(coordinateAtActorPosition(x, y).fillCircle(Distance(250, Meters)).area { true })
                    true
                } else {
                    false
                }
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if(event?.button == Buttons.LEFT) {
                    knownWorld.reveal(coordinateAtActorPosition(x, y))
                }
            }
        }
        val highlightCoordinateListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return if (button == Buttons.RIGHT) {
                    log.info { "My origin: $originX | $originY on stage ${localToStageCoordinates(Vector2(originX, originY))}" }
                    if(coordinateHighlightLabel.x == x && coordinateHighlightLabel.y == y) {
                        coordinateHighlightLabel.isVisible = false
                    } else {
                        val labelPosition = localToStageCoordinates(drawPositionOf(coordinateAtActorPosition(x, y)))
                        coordinateHighlightLabel.x = labelPosition.x
                        coordinateHighlightLabel.y = labelPosition.y
                        coordinateHighlightLabel.isVisible = true
                        coordinateHighlightLabel.setText(coordinateAtActorPosition(x, y).toString())
                    }
                    true
                } else {
                    false
                }
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                knownWorld.reveal(coordinateAtActorPosition(x, y))
            }
        }
        addListener(scrollListener)
        addListener(revealListener)
        addListener(highlightCoordinateListener)
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