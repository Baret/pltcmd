package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.game.serialization.Storage
import de.gleex.pltcmd.game.serialization.fileBasename
import de.gleex.pltcmd.model.world.WorldMap
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

/**
 * Stores and loads map data.
 */
@OptIn(ExperimentalTime::class)
object MapStorage {
    private val log = LoggerFactory.getLogger(MapStorage::class)
    private const val fileSuffix = ".map"

    /** Save the given map to the specified file. */
    fun save(map: WorldMap, fileId: String) {
        val fileName = toMapFile(fileId)
        log.debug("saving $map to $fileName")
        val duration = measureTime {
            val dao = WorldMapDao.of(map)
            Storage.save(dao, fileName)
        }
        log.info("saved $map to $fileName in $duration")
    }

    /** Loads the map from the given file. Returns null if the file does not exist. */
    fun load(fileId: String): WorldMap? {
        val fileName = toMapFile(fileId)
        val (map, duration) = measureTimedValue {
            val dao = Storage.load<WorldMapDao>(fileName)
            dao?.toMap()
        }
        log.info("loaded $map from $fileName in $duration")
        return map
    }

    private fun toMapFile(fileId: String) = fileId.fileBasename + fileSuffix
}