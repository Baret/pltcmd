import de.gleex.pltcmd.game.serialization.world.WorldMapDao
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import kotlin.random.Random
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
        if (!dataFolder.mkdirs()) {
            log.error("Failed to create folder ${dataFolder.absolutePath}")
        }
    }

    fun save(map: WorldMap, fileId: String) {
        val fileName = toMapFile(fileId)
        log.debug("saving $map to $fileName")
        val duration = measureTime {
            val dao = WorldMapDao.of(map)
            val json = Json.encodeToString(dao).toByteArray()
            val protoBuf = ProtoBuf.encodeToByteArray(dao)
            val cbor = Cbor.encodeToByteArray(dao)
            // TODO remove debug output
            println("saved map sizes:")
            println("JSON ${json.size} " + json.toString(Charset.defaultCharset()).substring(0, 100))
            println("Proto ${protoBuf.size} " + protoBuf.toString(Charset.defaultCharset()).substring(0, 100))
            println("CBOR ${cbor.size} " + cbor.toString(Charset.defaultCharset()).substring(0, 100))
            val file = File(dataFolder, fileName)
            file.writeBytes(protoBuf)
        }
        log.info("saved $map to $fileName in $duration")
    }

    private fun toMapFile(fileId: String) = fileId.fileName + ".map"

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

// TODO remove test application
fun main() {
    val origin = Coordinate(100, 250)
    val sectors = (0..4).map { i ->
        val sectorOrigin = origin.withRelativeEasting(i * Sector.TILE_COUNT)
        Sector(sectorOrigin, CoordinateRectangle(sectorOrigin, Sector.TILE_COUNT, Sector.TILE_COUNT).asSequence()
            .map { WorldTile(it, Terrain.random(Random)) }
            .toSortedSet())
    }
    val map = WorldMap.create(sectors)
    val fileId = "test"
    Storage.save(map, fileId)
    val loaded = Storage.loadMap(fileId)
    println("restored $loaded")
}