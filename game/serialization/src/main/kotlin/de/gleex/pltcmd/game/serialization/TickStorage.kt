package de.gleex.pltcmd.game.serialization

import de.gleex.pltcmd.game.ticks.TickId

/**
 * Stores the number of a tick and loads it as [TickId].
 */
object TickStorage: ModelStorage<TickId, Int>() {
    override val storageType = "tickid"

    override fun toDao(model: TickId) = model.value

    override fun toModel(dao: Int) = TickId(dao)

    override fun saveTyped(dao: Int, storage: StorageId) = Storage.save(dao, storage)

    override fun loadTyped(storage: StorageId): Int? = Storage.load(storage)
}