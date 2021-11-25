package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import kotlinx.serialization.Serializable

/** Stores/loads map data. */
@Serializable
data class WorldMapDao(val tiles: Collection<WorldTile>, val name: String = "legacy storage format") {

    /** Convert this data back to a model. */
    fun toMap(): WorldMap {
        return WorldMap.create(tiles.toSortedSet())
    }

    companion object {
        /** Returns only the basic data of the map */
        fun of(map: WorldMap, name: String): WorldMapDao {
            val tilesPerOrigin: List<WorldTile> = map.area.map { map.area[it].get() }
            return WorldMapDao(tilesPerOrigin, name)
        }
    }
}
