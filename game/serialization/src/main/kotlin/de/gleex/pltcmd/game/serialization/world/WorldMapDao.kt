package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.sectorOrigin
import kotlinx.serialization.Serializable

/** Stores/loads map data. */
@Serializable
data class WorldMapDao(val tiles: Collection<WorldTile>, val name: String = "legacy storage format") {

    /** Convert this data back to a model. */
    fun toMap(): WorldMap {
        val sectors = tiles
            .groupBy { it.sectorOrigin }
            .map { Sector(it.key, it.value.toSortedSet()) }
        return WorldMap.create(sectors)
    }

    companion object {
        /** Returns only the basic data of the map */
        fun of(map: WorldMap, name: String): WorldMapDao {
            val tilesPerOrigin = map.sectors.flatMap { sector -> sector.tiles.toList() }
            return WorldMapDao(tilesPerOrigin, name)
        }
    }
}
