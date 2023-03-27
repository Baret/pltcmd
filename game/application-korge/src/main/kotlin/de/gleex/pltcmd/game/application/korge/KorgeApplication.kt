package de.gleex.pltcmd.game.application.korge

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
import kotlin.random.Random

const val TILE_SIZE = 16

suspend fun main() {
    val world = WorldMapGenerator(Random.nextLong(), 100, 100)
        .generateWorld(Coordinate(100, 200))
    val mapWidth = world.width * TILE_SIZE
    val mapHeight = world.height * TILE_SIZE
    Korge("PltCmd", virtualWidth = mapWidth, virtualHeight = mapHeight) {
        val worldMapContainer = worldMap(world)
            .apply {
                onScroll { event ->
                    if (event.scrollDeltaYPixels < 0) {
                        if (scale <= 3.0) {
                            scale += 0.4
                        }
                    } else {
                        if (scale >= 1.0) {
                            scale -= 0.4
                        }
                    }
                }
            }
    }
}

/**
 * Draws the first sector of the given world.
 */
private fun Stage.worldMap(world: WorldMap): Container =
    fixedSizeContainer(Sector.TILE_COUNT * TILE_SIZE, Sector.TILE_COUNT * TILE_SIZE) {
        val sector = world.sectors.first()
        val containerPosX = 0
        val containerPosY = actualVirtualHeight - TILE_SIZE
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
