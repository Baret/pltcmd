package de.gleex.pltcmd.game.application.korge

import com.soywiz.kmem.toIntRound
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onScroll
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {  }

const val TILE_SIZE = 16

suspend fun main() {
    val world = WorldMapGenerator(Random.nextLong(), 100, 100)
        .generateWorld(Coordinate(100, 200))
    val mapWidth = world.width * TILE_SIZE
    val mapHeight = world.height * TILE_SIZE
    Korge("PltCmd", virtualWidth = mapWidth, virtualHeight = mapHeight) {
        val camera = camera {
            worldMap(world)
            scale = 2.0
        }

        onScroll { event ->
            if (event.scrollDeltaYPixels < 0) {
                if (camera.scale <= 3.0) {
                    camera.scale += 0.4
                }
            } else {
                if (camera.scale >= 1.0) {
                    camera.scale -= 0.4
                }
            }
        }
        addUpdater { deltaTime ->
            with(camera) {
                if (views.input.keys[Key.A]) x += TILE_SIZE * scaleX
                if (views.input.keys[Key.D]) x -= TILE_SIZE * scaleX
                if (views.input.keys[Key.W]) y += TILE_SIZE * scaleY
                if (views.input.keys[Key.S]) y -= TILE_SIZE * scaleY
            }
        }
    }
}

/**
 * Draws the first sector of the given world.
 */
private fun Container.worldMap(world: WorldMap): Container =
    fixedSizeContainer(Sector.TILE_COUNT * TILE_SIZE, Sector.TILE_COUNT * TILE_SIZE) {
        val sector = world.sectors.first()
        val containerPosX = 0
        val containerPosY = unscaledHeight.toIntRound() - TILE_SIZE
        val firstTile = sector.tiles.first()
        val firstContainer: Container = worldTileContainer(firstTile)
            .apply {
                position(containerPosX, containerPosY)
            }

        world
            .allTiles
            .drop(1)
            .fold(firstTile to firstContainer) { (prevTile, prevContainer), currentTile ->
                val currentContainer = worldTileContainer(currentTile)
                if (prevTile.coordinate.northingFromBottom == currentTile.coordinate.northingFromBottom) {
                    currentContainer.alignLeftToRightOf(prevContainer)
                    currentContainer.alignTopToTopOf(prevContainer)
                } else {
                    currentContainer.alignLeftToLeftOf(firstContainer)
                    currentContainer.alignBottomToTopOf(prevContainer)
                }
                currentTile to currentContainer
            }
    }

private fun Container.worldTileContainer(worldTile: WorldTile): Container = fixedSizeContainer(TILE_SIZE, TILE_SIZE) {
    // TODO: draw different tiles
    // then tint depending on height
    val color = when (worldTile.terrain.type) {
        TerrainType.GRASSLAND     -> Colors.GREEN
        TerrainType.FOREST        -> Colors.DARKGREEN
        TerrainType.HILL          -> Colors.BROWN
        TerrainType.MOUNTAIN      -> Colors.DARKGRAY
        TerrainType.WATER_DEEP    -> Colors.DARKBLUE
        TerrainType.WATER_SHALLOW -> Colors.BLUE
    }
    solidRect(TILE_SIZE, TILE_SIZE, color)
}
