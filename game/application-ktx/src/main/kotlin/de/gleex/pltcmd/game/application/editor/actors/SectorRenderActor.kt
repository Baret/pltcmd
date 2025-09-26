package de.gleex.pltcmd.game.application.editor.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.scenes.scene2d.Group
import de.gleex.pltcmd.game.application.drawing.drawWithType
import de.gleex.pltcmd.game.application.editor.worldSectorEdgeLengthInMeters
import de.gleex.pltcmd.game.application.editor.worldTileEdgeLengthInMeters
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangleSequence
import mu.KotlinLogging
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val log = KotlinLogging.logger { }

class SectorRenderActor(private val sectorOrigin: Coordinate, private val world: MutableWorld) : Group() {
    private val myCoordinates: CoordinateRectangleSequence =
        sectorOrigin..sectorOrigin.movedBy(Sector.TILE_COUNT, Sector.TILE_COUNT)

    private val renderer = ShapeRenderer()

    init {
        x = (sectorOrigin.eastingFromLeft - world.bottomLeftCoordinate.eastingFromLeft) * worldSectorEdgeLengthInMeters * Sector.TILE_COUNT
        y =
            (sectorOrigin.northingFromBottom - world.bottomLeftCoordinate.northingFromBottom) * worldSectorEdgeLengthInMeters * Sector.TILE_COUNT
        val size = Sector.TILE_COUNT * worldSectorEdgeLengthInMeters
        setSize(size, size)
//        for (coordinate in sectorOrigin..sectorOrigin.movedBy(Sector.TILE_COUNT, Sector.TILE_COUNT)) {
//            addActor(HeightRenderActor(coordinate).also { world.addListener(it) })
//        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (LogIWasRendered.logNow.load()) {
            log.info { " = = = Rendering now sector $sectorOrigin" }
        }
        batch?.end()

        renderer.transformMatrix = batch?.transformMatrix
        renderer.projectionMatrix = batch?.projectionMatrix
        renderer.drawWithType(Filled) {
            myCoordinates.forEach { coordinate ->
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

                val drawPos = sectorOrigin - coordinate
                rect(
                    drawPos.eastingFromLeft .toFloat(),
                    drawPos.northingFromBottom.toFloat(),
                    worldTileEdgeLengthInMeters,
                    worldTileEdgeLengthInMeters
                )
            }
        }

        batch?.begin()
    }
}