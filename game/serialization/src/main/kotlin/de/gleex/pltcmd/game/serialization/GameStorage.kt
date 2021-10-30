package de.gleex.pltcmd.game.serialization

import de.gleex.pltcmd.game.serialization.world.MapStorage

object GameStorage {

    /** map of all stored games to their names */
    val list: Map<StorageId, String>
        get() {
            return Storage.listAll(MapStorage.storageType)
                .associateWith { it.id }
        }

}