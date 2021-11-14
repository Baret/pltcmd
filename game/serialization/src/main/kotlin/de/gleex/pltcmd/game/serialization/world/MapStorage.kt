package de.gleex.pltcmd.game.serialization.world

import de.gleex.pltcmd.game.serialization.ModelStorage
import de.gleex.pltcmd.game.serialization.Storage
import de.gleex.pltcmd.game.serialization.StorageId
import de.gleex.pltcmd.model.world.WorldMap

/**
 * Stores and loads map data.
 */
object MapStorage : ModelStorage<WorldMap, WorldMapDao>() {
    override val storageType = "map"

    override fun saveTyped(dao: WorldMapDao, storage: StorageId) = Storage.save(dao, storage)

    override fun loadTyped(storage: StorageId) = Storage.load<WorldMapDao>(storage)

    override fun toDao(model: WorldMap) = WorldMapDao.of(model)

    override fun toModel(dao: WorldMapDao) = dao.toMap()

}