package de.gleex.pltcmd.game.serialization

import mu.KotlinLogging
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {}

/**
 * Implements the basic storage of a model object. Subclasses must specify the identifier of the storage and may provide
 * a more specialized DAO.
 *
 * @param M type of model to persist
 * @param D type of data access object (DAO)
 */
abstract class ModelStorage<M : Any, D : Any> {
    abstract val storageType: String

    /** Save the given model under the given id. */
    @OptIn(ExperimentalTime::class)
    fun save(model: M, gameId: String) {
        val storage = gameId.storageId
        log.debug { "saving $model to $storage" }
        val duration = measureTime {
            val dao = toDao(model)
            saveTyped(dao, storage)
        }
        log.info { "saved $model to ${storage.id} in $duration" }
    }

    /** Loads the model for the given id. Returns null if no model is stored under that id. */
    fun load(gameId: String): M? {
        return load(gameId.storageId)
    }

    /** Loads the model for the given id. Returns null if no model is stored under that id. */
    @OptIn(ExperimentalTime::class)
    fun load(storage: StorageId): M? {
        val (model, duration) = measureTimedValue {
            val dao = loadTyped(storage) ?: return null
            toModel(dao)
        }
        log.info { "loaded $model from ${storage.id} in $duration" }
        return model
    }

    // these are necessary as kotlinx-serialization uses reified types only for type safe serializers.
    // And those cannot be inlined with generics used here. Other methods that accept Type/KType only return Any :(
    abstract fun saveTyped(dao: D, storage: StorageId)
    abstract fun loadTyped(storage: StorageId): D?

    abstract fun toDao(model: M): D
    abstract fun toModel(dao: D): M

    private val String.storageId
        get() = StorageId(this, storageType)

}