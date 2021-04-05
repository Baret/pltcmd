import de.gleex.pltcmd.game.serialization.world.WorldMapDao
import de.gleex.pltcmd.model.world.WorldMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

/**
 * Manages persistent data.
 */
@OptIn(ExperimentalTime::class, ExperimentalSerializationApi::class)
object Storage {
    private val log = LoggerFactory.getLogger(Storage::class)
    private val dataFolder = File("data")

    init {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            log.error("Failed to create folder ${dataFolder.absolutePath}")
        }
    }

    /** Save the given map to the specified file. */
    fun save(map: WorldMap, fileId: String) {
        val fileName = toMapFile(fileId)
        log.debug("saving $map to $fileName")
        val duration = measureTime {
            val dao = WorldMapDao.of(map)
            val bytes = ProtoBuf.encodeToByteArray(dao)
            val file = File(dataFolder, fileName)
            file.writeBytes(bytes)
        }
        log.info("saved $map to $fileName in $duration")
    }

    private fun toMapFile(fileId: String) = fileId.fileName + ".map"

    /** Loads the map from the given file. Returns null if the file does not exist. */
    fun loadMap(fileId: String): WorldMap? {
        val fileName = toMapFile(fileId)
        val (map, duration) = measureTimedValue {
            val file = File(dataFolder, fileName)
            if (!file.exists()) {
                return null
            }
            val bytes = file.readBytes()
            val dao = ProtoBuf.decodeFromByteArray<WorldMapDao>(bytes)
            dao.toMap()
        }
        log.info("loaded $map from $fileName in $duration")
        return map
    }
}

/** Remove path navigation from name like "../../root" */
val String.fileName: String
    get() = File(this).nameWithoutExtension
