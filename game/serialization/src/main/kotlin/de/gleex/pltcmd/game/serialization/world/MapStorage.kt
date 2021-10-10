package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.game.serialization.Storage
import de.gleex.pltcmd.game.serialization.StorageId
import de.gleex.pltcmd.model.world.WorldMap
import mu.KotlinLogging
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {}

/**
 * Stores and loads map data.
 */
@OptIn(ExperimentalTime::class)
object MapStorage {
    private const val storageType = "map"

    /** map of all stored maps to their names */
    val list: Map<StorageId, String>
        get() {
            return Storage.listAll(storageType)
                .associateWith { Storage.load<WorldMapDao>(it)?.name ?: "missing ${it.id}" }
        }

    /** Save the given map under the given id. */
    fun save(map: WorldMap, mapId: String) {
        val storage = mapId.storageId
        log.debug { "saving $map to $storage" }
        val duration = measureTime {
            val dao = WorldMapDao.of(map, mapId)
            Storage.save(dao, storage)
        }
        log.info { "saved $map to ${storage.id} in $duration" }
    }

    /** Loads the map for the given id. Returns null if no map is stored under that id. */
    fun load(mapId: String): WorldMap? {
        return load(mapId.storageId)
    }

    /** Loads the map for the given id. Returns null if no map is stored under that id. */
    fun load(storage: StorageId): WorldMap? {
        val (map, duration) = measureTimedValue {
            val dao = Storage.load<WorldMapDao>(storage)
            dao?.toMap()
        }
        log.info("loaded $map from ${storage.id} in $duration")
        return map
    }

    private val String.storageId
        get() = StorageId(this, storageType)
}