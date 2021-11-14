package de.gleex.pltcmd.game.serialization

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.random.Random

/**
 * Stores and loads [Random]. It uses Java object serialization to persist the state of [Random].
 */
object RandomStorage: ModelStorage<Random, ByteArray>() {
    override val storageType = "random"

    override fun saveTyped(dao: ByteArray, storage: StorageId) = Storage.save(dao, storage)

    override fun loadTyped(storage: StorageId): ByteArray? = Storage.load(storage)

    override fun toDao(model: Random): ByteArray {
        // because we cannot add @Serializable to kotlin.random.Random and don't know the actual implementation we just store it as bytes
        val byteStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteStream)
        objectOutputStream.writeObject(model)
        objectOutputStream.close()
        return byteStream.toByteArray()
    }

    override fun toModel(dao: ByteArray): Random {
        val byteStream = ByteArrayInputStream(dao)
        return ObjectInputStream(byteStream).readObject() as Random
    }
}