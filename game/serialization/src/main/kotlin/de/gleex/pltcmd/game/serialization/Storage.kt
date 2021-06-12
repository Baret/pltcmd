package de.gleex.pltcmd.game.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File

/**
 * Manages persistent data.
 */
@OptIn(ExperimentalSerializationApi::class)
internal object Storage {
    private val log = LoggerFactory.getLogger(Storage::class)
    private val dataFolder = File("data")

    init {
        require(dataFolder.canWrite() || dataFolder.mkdirs()) {
            "Failed to create folder ${dataFolder.absolutePath}"
        }
    }

    /** Stores the given object in a file with the id. */
    inline fun <reified T> save(dao: T, id: StorageId) {
        val bytes = ProtoBuf.encodeToByteArray(dao)
        val file = id.file
        file.writeBytes(bytes)
    }

    /** Loads the object from the identified storage (file). */
    inline fun <reified R> load(id: StorageId): R? {
        val file = id.file
        if (!file.canRead()) {
            log.warn("Cannot load data from $file because it is not readable!")
            return null
        }
        val bytes = file.readBytes()
        return ProtoBuf.decodeFromByteArray<R>(bytes)
    }

    private val StorageId.file
        get() = File(dataFolder, sanitizeFilename(id) + "." + type)
}

/** Create a String safe for file names that are almost as unique as the given text */
fun sanitizeFilename(text: String): String {
    val bytes = text.toByteArray(Charsets.UTF_8)
    val hex = bytes.toHex()
    // maximum filename length is 255 bytes https://en.wikipedia.org/wiki/Comparison_of_file_systems#Limits
    return hex.take(250)
}

// from https://www.javacodemonk.com/md5-and-sha256-in-java-kotlin-and-android-96ed9628
fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}
