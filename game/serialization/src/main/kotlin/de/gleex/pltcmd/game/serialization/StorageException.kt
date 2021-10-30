package de.gleex.pltcmd.game.serialization

/** Unexpected exception thrown when working with serialized data. */
class StorageException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String?) : this(message, null)
}