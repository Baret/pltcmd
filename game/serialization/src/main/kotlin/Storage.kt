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
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.nio.charset.Charset
import kotlin.random.Random

/**
 * Manages persistent data.
 */
object Storage {
    private val fileMap = File("map.ser")

    @OptIn(ExperimentalSerializationApi::class)
    fun save(map: WorldMap) {
        println("saving $map")
        val dao = WorldMapDao.of(map)
        val protoBuf = ProtoBuf.encodeToByteArray(dao)
        val cbor = Cbor.encodeToByteArray(dao)
        // TODO remove debug output
        println("saved map sizes:")
        println("Proto ${protoBuf.size} " + protoBuf.toString(Charset.defaultCharset()).substring(0, 100))
        println("CBOR ${cbor.size} " + cbor.toString(Charset.defaultCharset()).substring(0, 100))
        fileMap.writeBytes(protoBuf)
    }

    fun loadMap(): WorldMap {
        val bytes = fileMap.readBytes()
        val dao = ProtoBuf.decodeFromByteArray<WorldMapDao>(bytes)
        return dao.toMap()
    }
}

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
    Storage.save(map)
    val loaded = Storage.loadMap()
    println("restored $loaded")
}