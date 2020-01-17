package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.impl.Position3D
import org.hexworks.zircon.api.game.GameArea

class GameWorld(private val worldMap: WorldMap): GameArea<Tile, MapBlock> by GameAreaBuilder.newBuilder<Tile, MapBlock>().
        withActualSize(Sizes.create3DSize(GameOptions.SECTORS_COUNT_H * Sector.TILE_COUNT, GameOptions.SECTORS_COUNT_V * Sector.TILE_COUNT, 1)).
        withVisibleSize(Sizes.create3DSize(Sector.TILE_COUNT, Sector.TILE_COUNT, 1)).
        withLayersPerBlock(MapBlock.LAYERS_PER_BLOCK).
        withDefaultBlock(MapBlock(Terrain(TerrainType.GRASSLAND, TerrainHeight.ONE))).
        build()
{
    companion object {
        private val log = LoggerFactory.getLogger(GameWorld::class)
    }

    init {
        worldMap.sectors.forEach(::putSector)
        log.debug("Created GameWorld with ${worldMap.sectors.size} sectors. Visible size = ${visibleSize()}")
    }

    private fun putSector(sector: Sector) {
        sector.tiles.forEach {
            val position = it.coordinate.toPosition()
            val block = MapBlock(it.terrain)
            setBlockAt(position, block)
        }
    }

    private fun Coordinate.toPosition() = Position3D.create(eastingFromLeft, northingFromBottom, 0)

    private fun Position3D.toCoordinate() = Coordinate(x, y)

    fun visibleMapOffset(): Coordinate {
        return visibleOffset().toCoordinate()
    }

}
