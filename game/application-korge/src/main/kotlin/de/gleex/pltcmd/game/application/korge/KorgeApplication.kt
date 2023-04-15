package de.gleex.pltcmd.game.application.korge

import com.soywiz.kmem.toIntRound
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.draggable
import com.soywiz.korge.input.onScroll
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.format.SVG
import com.soywiz.korim.vector.format.readSVG
import com.soywiz.korim.vector.render
import com.soywiz.korim.vector.scaled
import com.soywiz.korio.file.std.resourcesVfs
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.WorldMapGenerator
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger { }

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
private suspend fun Container.worldMap(world: WorldMap): Container =
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

        val unit_svg = resourcesVfs["unit_symbols_inkscape.svg"].readSVG()
        debugSvg(unit_svg)
        log.info { " - - - -" }
            debugSvg(resourcesVfs["unit_symbols_inkscape_opt.svg"].readSVG())
        image(unit_svg.render())
            .draggable {  }
        image(unit_svg.scaled(0.5,0.5).render())
            .position(200, 0)
            .draggable {  }
        image(unit_svg.scaled(2.6, 2.6).render())
            .position(250, 0)
            .draggable {  }
    }

private fun debugSvg(unit_svg: SVG) {
    log.info { "${unit_svg.defs.size} defs:" }
    unit_svg.defs.forEach { (t, u) -> log.info { "\t$t\t$u" } }
    log.info { "root: ${unit_svg.root}" }
    SVG.parseAttributesAndStyles(unit_svg.root).forEach { (k, v) -> log.info { "\t$k\t$v" } }
}

private suspend fun Container.worldTileContainer(worldTile: WorldTile): Container = fixedSizeContainer(TILE_SIZE, TILE_SIZE) {
    // TODO: draw different tiles
    // then tint depending on height
    val heightFactor = when (worldTile.height) {
        TerrainHeight.ONE   -> 0.1
        TerrainHeight.TWO   -> 0.2
        TerrainHeight.THREE -> 0.3
        TerrainHeight.FOUR  -> 0.4
        TerrainHeight.FIVE  -> 0.5
        TerrainHeight.SIX   -> 0.6
        TerrainHeight.SEVEN -> 0.7
        TerrainHeight.EIGHT -> 0.8
        TerrainHeight.NINE  -> 0.9
        TerrainHeight.TEN   -> 1.0
    }
    val bgColor = when (worldTile.terrain.type) {
        TerrainType.GRASSLAND     -> Colors.GREEN
        TerrainType.FOREST        -> Colors.DARKGREEN
        TerrainType.HILL          -> Colors.BROWN
        TerrainType.MOUNTAIN      -> Colors.DARKGRAY
        TerrainType.WATER_DEEP    -> Colors.DARKBLUE
        TerrainType.WATER_SHALLOW -> Colors.BLUE
    }.apply { alpha(heightFactor) }
    val fgColor = when (worldTile.terrain.type) {
        TerrainType.GRASSLAND     -> Colors.LIGHTGREEN
        TerrainType.FOREST        -> Colors.FORESTGREEN
        TerrainType.HILL          -> Colors.SANDYBROWN
        TerrainType.MOUNTAIN      -> Colors.DIMGRAY
        TerrainType.WATER_DEEP    -> Colors.DEEPSKYBLUE
        TerrainType.WATER_SHALLOW -> Colors.LIGHTBLUE
    }
    solidRect(TILE_SIZE, TILE_SIZE, bgColor)
}
