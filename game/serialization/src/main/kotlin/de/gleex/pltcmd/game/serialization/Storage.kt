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
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            log.error("Failed to create folder ${dataFolder.absolutePath}")
        }
    }

    /** Stores the given object in a file with the given name. */
    inline fun <reified T> save(dao: T, fileName: String) {
        val bytes = ProtoBuf.encodeToByteArray(dao)
        val file = File(dataFolder, fileName)
        file.writeBytes(bytes)
    }

    /** Loads the object from the given file. */
    inline fun <reified R> load(fileName: String): R? {
        val file = File(dataFolder, fileName)
        if (!file.canRead()) {
            log.warn("Cannot load data from $file because it is not readable!")
            return null
        }
        val bytes = file.readBytes()
        return ProtoBuf.decodeFromByteArray<R>(bytes)
    }
}

/** Remove path navigation from name like "../../root" and an extension */
val String.fileBasename: String
    get() = File(this).nameWithoutExtension
