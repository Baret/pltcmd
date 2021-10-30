package de.gleex.pltcmd.game.serialization

import de.gleex.pltcmd.model.faction.Faction

/**
 * Stores and loads factions.
 */
object FactionStorage : DirectModelStorage<List<Faction>>() {
    override val storageType = "factions"

    override fun saveTyped(dao: List<Faction>, storage: StorageId) = Storage.save(dao, storage)

    override fun loadTyped(storage: StorageId) = Storage.load<List<Faction>>(storage)

}