package de.gleex.pltcmd.game.application.editor.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.scenes.scene2d.Actor
import de.gleex.pltcmd.game.application.drawing.drawWithType
import de.gleex.pltcmd.game.application.editor.worldTileEdgeLengthInMeters
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val log = KotlinLogging.logger { }

class HeightRenderActor(private val coordinate: Coordinate) : Actor(), MapGenerationListener {

    private val renderer = ShapeRenderer()

    @OptIn(ExperimentalAtomicApi::class)
    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (LogIWasRendered.logNow.load()) {
            log.info { " = = = Rendering now $coordinate" }
        }
        batch?.end()

        renderer.transformMatrix = batch?.transformMatrix
        renderer.projectionMatrix = batch?.projectionMatrix
        renderer.drawWithType(Filled) {
            color = if (coordinate.eastingFromLeft % 2 == 0) {
                if (coordinate.northingFromBottom % 2 == 0) {
                    Color.BLACK
                } else {
                    Color.LIGHT_GRAY
                }
            } else {
                if (coordinate.northingFromBottom % 2 == 0) {
                    Color.LIGHT_GRAY
                } else {
                    Color.BLACK
                }
            }
            rect(x, y, worldTileEdgeLengthInMeters, worldTileEdgeLengthInMeters)
        }

        batch?.begin()
    }

    override fun startGeneration(origin: Coordinate) {
        val x = (coordinate.eastingFromLeft - origin.eastingFromLeft) * worldTileEdgeLengthInMeters
        val y =
            (coordinate.northingFromBottom - origin.northingFromBottom) * worldTileEdgeLengthInMeters
        setPosition(x, y)
        width = worldTileEdgeLengthInMeters
        height = worldTileEdgeLengthInMeters
        log.info { "$coordinate | initialized actor at $x | $y with size $width * $height" }
    }

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        TODO("Not yet implemented")
    }
}
