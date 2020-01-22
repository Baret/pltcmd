package de.gleex.pltcmd.game

import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.options.GameOptions
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.internal.game.impl.TopDownProjectionStrategy

class GameWorld(worldMap: WorldMap):
        BaseGameArea<Tile, MapBlock>(
                Size3D.create(Sector.TILE_COUNT, Sector.TILE_COUNT, 1),
                GameOptions.WORLD_SIZE,
                mapOf(),
                TopDownProjectionStrategy())
{
    companion object {
        private val log = LoggerFactory.getLogger(GameWorld::class)
    }

    init {
        worldMap.sectors.forEach(::putSector)
        log.debug("Created GameWorld with ${worldMap.sectors.size} sectors. Visible size = $visibleSize")
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

    /** Returns the [Coordinate] of the [Tile] that is visible in the top left corner. */
    fun visibleTopLeftCoordinate(): Coordinate {
        return visibleOffset.toCoordinate()
    }

    fun scrollToCoordinate(coord: Coordinate) {
        scrollTo(coord.toPosition())
    }

}
