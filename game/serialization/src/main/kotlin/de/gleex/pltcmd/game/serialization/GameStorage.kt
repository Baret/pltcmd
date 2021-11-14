package de.gleex.pltcmd.game.serialization

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.asFactionEntity
import de.gleex.pltcmd.game.engine.entities.types.faction
import de.gleex.pltcmd.game.serialization.world.MapStorage
import de.gleex.pltcmd.model.faction.Faction
import org.hexworks.amethyst.api.entity.EntityType

object GameStorage {

    /** map of all stored games to their names */
    val list: Map<StorageId, String>
        get() {
            return Storage.listAll(MapStorage.storageType)
                .associateWith { it.id }
        }

    fun save(game: GameContext, gameId: String) {
        MapStorage.save(game.world, gameId)
        FactionStorage.save(game.factions, gameId)
        TickStorage.save(game.currentTick, gameId)
        RandomStorage.save(game.random, gameId)
        //EntityStorage.save(game.entities, gameId)
    }

    private val GameContext.factions: MutableList<Faction>
        get() {
            // TODO save also factions without entities
            val factions =
                entities.mapNotNull { entity ->
                    entity.asFactionEntity<Faction?> {
                        it.faction.value
                    }.orElse(null)
                }
                    // remove duplicates
                    .toSet()
                    .toMutableList()
            // move player faction to first position
            factions.remove(playerFaction)
            factions.add(0, playerFaction)

            return factions
        }

    fun load(gameId: String): Pair<GameContext, List<Faction>> {
        val random = RandomStorage.load(gameId) ?: throw StorageException("failed to load random of $gameId")
        val tick = TickStorage.load(gameId) ?: throw StorageException("failed to load tick id of $gameId")
        val factions = FactionStorage.load(gameId) ?: throw StorageException("failed to load factions of $gameId")
        val map = MapStorage.load(gameId) ?: throw StorageException("failed to load map of $gameId")
        // TODO save entities
        val entities = EntitySet<EntityType>()

        val context = GameContext(tick, map, factions[0], entities, random)
        return Pair(context, factions)
    }
}

fun GameContext.save(gameId: String) {
    GameStorage.save(this, gameId)
}