package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import kotlinx.collections.immutable.toPersistentMap
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.game.base.BaseGameArea

/**
 * A [GameArea] that shows a map with holes. It uses [IncompleteMapBlock] for that.
 * The full map is visible and can be altered.
 */
class IncompleteMapGameArea(size: Size) :
        BaseGameArea<Tile, IncompleteMapBlock>(
                initialVisibleSize = Size3D.from2DSize(size, 1),
                initialActualSize = Size3D.from2DSize(size, 1),
                initialContents = size.fetchPositions()
                        .map { it.to3DPosition(0) }
                        .associateWith { IncompleteMapBlock() }
                        .toPersistentMap()) {

    fun updateBlock(position: Position3D, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        blocks[position]?.setTerrain(terrainHeight, terrainType)
    }

}