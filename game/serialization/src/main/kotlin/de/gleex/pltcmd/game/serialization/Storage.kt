package de.gleex.pltcmd.game.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import mu.KotlinLogging
import java.io.File

private val log = KotlinLogging.logger {}

/**
 * Manages persistent data.
 */
@OptIn(ExperimentalSerializationApi::class)
internal object Storage {
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

    /** lists all stored entries of the given type */
    fun listAll(type: String): List<StorageId> {
        return dataFolder.listFiles { file -> file.extension == type }
            .map {
                val sanitizedName = it.nameWithoutExtension
                val id = unsanitizeName(sanitizedName)
                StorageId(id, type)
            }
    }

    private val StorageId.file
        get() = File(dataFolder, sanitizeFilename(id) + "." + type)
}

/** Create a String safe for file names that are almost as unique as the given text */
fun sanitizeFilename(text: String): String {
    val bytes = text.toByteArray(Charsets.UTF_8)
    val hex = bytes.toHex()
    // maximum filename length is 255 bytes https://en.wikipedia.org/wiki/Comparison_of_file_systems#Limits
    // we leave some space for prefix and file extension
    return hex.take(110)
}

fun unsanitizeName(fileName: String): String {
    val bytes = fileName.decodeHex()
    return String(bytes, Charsets.UTF_8)
}

// from https://www.javacodemonk.com/md5-and-sha256-in-java-kotlin-and-android-96ed9628
fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

// from https://stackoverflow.com/a/66614516
fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}