package de.gleex.pltcmd.game.application.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.VisUI
import de.gleex.pltcmd.game.application.actors.terrain.model.DrawableWorldTile
import de.gleex.pltcmd.game.application.actors.terrain.model.NeighborBitmap
import de.gleex.pltcmd.game.engine.attributes.memory.KnownTerrain
import de.gleex.pltcmd.game.engine.attributes.memory.KnownWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.fillCircle
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit.Meters
import ktx.graphics.copy
import mu.KotlinLogging
import kotlin.math.floor
import kotlin.properties.Delegates

private val log = KotlinLogging.logger { }

/**
 * Renders the given [KnownWorld] and offers basic controls for the user like scrolling.
 */
class WorldMapRendererActor(private val knownWorld: KnownWorld) : Group() {
    private val renderer = ShapeRenderer()

    private var bottomLeftCoordinate: Coordinate = knownWorld.origin.origin

    private val coordinateHighlightLabel = Label(bottomLeftCoordinate.toString(), VisUI.getSkin())

    private var worldWidthInMeters by Delegates.notNull<Float>()
    private var worldHeightInMeters by Delegates.notNull<Float>()

    private val tileCache: MutableMap<Coordinate, DrawableWorldTile> = HashMap(knownWorld.origin.allTiles.size / 8, 0.9f)

    init {
        setupListeners()
        addActor(coordinateHighlightLabel)
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        log.debug { "Stage has been set. Initializing values." }
        coordinateHighlightLabel.isVisible = false
        color = Color.DARK_GRAY
        val metersW = 50 * TILE_WIDTH
        val aspectRatio = width / height
        log.debug { "Setting cam to display $metersW meters, aspectRatio is $aspectRatio. And translating by $x | $y" }
        worldWidthInMeters = metersW * aspectRatio
        worldHeightInMeters = 50 * TILE_HEIGHT
        log.debug { "My origin: $originX | $originY on stage ${localToStageCoordinates(Vector2(originX, originY))}" }
        log.debug { "My size: $width * $height" }
        forEachVisibleCoordinate {
            knownWorld.reveal(it)
        }
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
            drawTile(knownWorld.drawableTileFor(currentCoordinate))
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

    private fun ShapeRenderer.drawTile(tile: DrawableWorldTile) {
        val drawPos = drawPositionOf(tile.coordinate)
        // Terrain type
        terrainType(tile, drawPos)
        // Terrain height
        contourLines(tile, drawPos)
    }

    private fun ShapeRenderer.terrainType(
        tile: DrawableWorldTile,
        drawPos: Vector2
    ) {
        val heightFactor = when (tile.terrain?.height) {
            TerrainHeight.ONE   -> 0.28f
            TerrainHeight.TWO   -> 0.36f
            TerrainHeight.THREE -> 0.44f
            TerrainHeight.FOUR  -> 0.52f
            TerrainHeight.FIVE  -> 0.60f
            TerrainHeight.SIX   -> 0.68f
            TerrainHeight.SEVEN -> 0.76f
            TerrainHeight.EIGHT -> 0.84f
            TerrainHeight.NINE  -> 0.92f
            TerrainHeight.TEN   -> 1.0f
            null                -> 1.0f
        }
        val terrainTypeColor: Color? = tile
            .terrain
            ?.type
            ?.let { terrainTypeColor[it] }
            ?.copy()
            ?.mul(heightFactor)
        if (terrainTypeColor != null) {
            drawWithType(ShapeRenderer.ShapeType.Filled) {
                color = terrainTypeColor
                rect(drawPos.x, drawPos.y, TILE_WIDTH, TILE_HEIGHT)
            }
        }
    }

    private fun ShapeRenderer.contourLines(
        tile: DrawableWorldTile,
        drawPos: Vector2
    ) {
        tile.terrain?.height?.let { localHeight ->
            val contourLineColor = if(localHeight.value % 2 == 0) { Color(.25f, .20f, .20f, 1f) } else { Color(.35f, .30f, .30f, 1f) }
            if (tile.neighborsLower.all()) {
                drawWithType(ShapeRenderer.ShapeType.Line) {
                    color = contourLineColor
                    rect(
                        drawPos.x + OFFSET_FROM_TILE_EDGE,
                        drawPos.y + OFFSET_FROM_TILE_EDGE,
                        TILE_WIDTH - (OFFSET_FROM_TILE_EDGE * 2f),
                        TILE_HEIGHT - (OFFSET_FROM_TILE_EDGE * 2f)
                    )
                }
                drawWithType(ShapeRenderer.ShapeType.Filled) {
                    color = contourLineColor
                    val triangleHeight = 6f
                    val triangleWidth = 4f
                    val centerX = TILE_WIDTH / 2f
                    val centerY = TILE_HEIGHT / 2f
                    val offsetX = triangleWidth / 2f
                    val offsetY = triangleHeight / 2f
                    triangle(
                        drawPos.x + (centerX - offsetX),
                        drawPos.y + (centerY - offsetY),
                        drawPos.x + (centerX + offsetX),
                        drawPos.y + (centerY - offsetY),
                        drawPos.x + centerX,
                        drawPos.y + (centerY + offsetY)
                        )
                }
            } else {
                drawWithType(ShapeRenderer.ShapeType.Line) {
                    tile.contourLines.forEach { contourLine ->
                        val lineStart = Vector2(drawPos.x + contourLine.x, drawPos.y + contourLine.y)
                        val lineEnd = Vector2(drawPos.x + contourLine.z, drawPos.y + contourLine.w)
                        color = contourLineColor
                        line(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y)
                    }
                }
            }
        }
    }

    private fun ShapeRenderer.fillBackground() {
        drawWithType(ShapeRenderer.ShapeType.Filled) {
            color = this@WorldMapRendererActor.color
            rect(0f, 0f, width, width)
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
                if (currentCoordinate.northingFromBottom == currentCoordinate.sectorOrigin.northingFromBottom) {
                line(drawPos, bottomRight)
                    }
                color = if (currentCoordinate.eastingFromLeft == currentCoordinate.sectorOrigin.eastingFromLeft) {
                    Color.RED
                } else {
                    Color.BLACK
                }
                val topLeft = Vector2(drawPos).add(0f, TILE_HEIGHT)
                if (currentCoordinate.eastingFromLeft == currentCoordinate.sectorOrigin.eastingFromLeft) {
                    line(drawPos, topLeft)
                }
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

    private fun KnownWorld.drawableTileFor(coordinate: Coordinate): DrawableWorldTile {
        if(tileCache.containsKey(coordinate)) {
            return tileCache[coordinate]!!
        }
        val knownTile: KnownTerrain = get(coordinate)
        val lowerNeighbors = buildList<NeighborBitmap.Direction> {
            knownTile.terrain?.height?.let { height ->
                NeighborBitmap.Direction.entries
                    .forEach { direction ->
                        val neighborHeight = get(coordinate.movedBy(direction.eastingDiff, direction.northingDiff)).terrain?.height
                        if (neighborHeight != null && neighborHeight.value < height.value) {
                            add(direction)
                        }
                    }
            }
        }
        val neighborsLower = NeighborBitmap.of(lowerNeighbors)
        val neighborsRevealed = buildList<NeighborBitmap.Direction> {
                NeighborBitmap.Direction.entries
                    .forEach { direction ->
                        if(get(coordinate.movedBy(direction.eastingDiff, direction.northingDiff)).revealed) {
                            add(direction)
                        }
                    }
        }

        // contour lines
        val contourLines = mutableListOf<Vector4>()
        if(knownTile.revealed) {
            val localHeight = knownTile.terrain?.height!!
            if (neighborsLower.isNorth()) {
                val nHeight = knownWorld[coordinate.withRelativeNorthing(1)].terrain?.height
                if (nHeight != null && localHeight > nHeight) {
                    contourLines.add(
                        Vector4(
                            0f,
                            TILE_HEIGHT - OFFSET_FROM_TILE_EDGE,
                            TILE_WIDTH,
                            TILE_HEIGHT - OFFSET_FROM_TILE_EDGE
                        )
                    )
                }
            }
            if (neighborsLower.isSouth()) {
                val nHeight = knownWorld[coordinate.withRelativeNorthing(-1)].terrain?.height
                if (nHeight != null && localHeight > nHeight) {
                    contourLines.add(
                        Vector4(
                            0f,
                            OFFSET_FROM_TILE_EDGE,
                            TILE_WIDTH,
                            OFFSET_FROM_TILE_EDGE
                        )
                    )
                }
            }
            if (neighborsLower.isEast()) {
                val nHeight = knownWorld[coordinate.withRelativeEasting(1)].terrain?.height
                if (nHeight != null && localHeight > nHeight) {
                    contourLines.add(
                        Vector4(
                            TILE_WIDTH - OFFSET_FROM_TILE_EDGE,
                            TILE_HEIGHT,
                            TILE_WIDTH - OFFSET_FROM_TILE_EDGE,
                            0f
                        )
                    )
                }
            }
            if (neighborsLower.isWest()) {
                val nHeight = knownWorld[coordinate.withRelativeEasting(-1)].terrain?.height
                if (nHeight != null && localHeight > nHeight) {
                    contourLines.add(
                        Vector4(
                            OFFSET_FROM_TILE_EDGE,
                            TILE_HEIGHT,
                            OFFSET_FROM_TILE_EDGE,
                            0f
                        )
                    )
                }
            }
        }

        val drawableWorldTile = DrawableWorldTile(
            knownTile,
            neighborsLower,
            NeighborBitmap.of(),
            NeighborBitmap.of(neighborsRevealed),
            contourLines
        )
        if(drawableWorldTile.neighborsRevealed.all()) {
            log.info { "All neighbors of $coordinate revealed, caching drawable tile." }
            tileCache[coordinate] = drawableWorldTile
        }
        return drawableWorldTile
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
                    val coordinateAtActorPosition = coordinateAtActorPosition(x, y)
                    log.debug { "You clicked at $x | $y which results in coordinate $coordinateAtActorPosition" }
                    if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                        knownWorld.reveal(knownWorld.origin.sectorAt(coordinateAtActorPosition))
                    } else {
                        knownWorld.reveal(coordinateAtActorPosition.fillCircle(Distance(250, Meters)).area { true })
                    }
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
            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                    val coordinateAtActorPosition = coordinateAtActorPosition(x, y)
                    val drawPositionOfCoordinate = drawPositionOf(coordinateAtActorPosition)
                    if(coordinateHighlightLabel.x == x && coordinateHighlightLabel.y == y) {
                        coordinateHighlightLabel.isVisible = false
                    } else {
                        val labelPosition = localToStageCoordinates(drawPositionOfCoordinate)
                        val labelText = "$coordinateAtActorPosition${knownWorld[coordinateAtActorPosition].terrain?.height?.let{", ${it.value}"} ?: ""}"
                        coordinateHighlightLabel.x = labelPosition.x
                        coordinateHighlightLabel.y = labelPosition.y
                        coordinateHighlightLabel.isVisible = true
                        coordinateHighlightLabel.setText(labelText)
                    }
                    return true
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
        private const val OFFSET_FROM_TILE_EDGE = 2.0f
    }
}