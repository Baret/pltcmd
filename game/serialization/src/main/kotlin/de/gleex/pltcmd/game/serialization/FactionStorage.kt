package de.gleex.pltcmd.game.serialization

import de.gleex.pltcmd.model.faction.Faction

/**
 * Stores and loads factions.
 */
object FactionStorage : DirectModelStorage<Faction>() {
    override val storageType = "faction"

    override fun saveTyped(dao: Faction, storage: StorageId) = Storage.save(dao, storage)

    override fun loadTyped(storage: StorageId) = Storage.load<Faction>(storage)

}