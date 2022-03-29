package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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
            val tilesPerOrigin: MutableList<WorldTile> = mutableListOf()
            runBlocking {
                map.allTiles.asFlow().flowOn(Dispatchers.Default).buffer().toList(tilesPerOrigin)
            }
            return WorldMapDao(tilesPerOrigin, name)
        }
    }
}
