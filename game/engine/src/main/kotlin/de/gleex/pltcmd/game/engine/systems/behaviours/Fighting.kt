package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.model.faction.Affiliation
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Attacks nearby enemies. */
internal object Fighting :
    BaseBehavior<GameContext>(ShootersAttribute::class, PositionAttribute::class, ElementAttribute::class) {

    private val log = LoggerFactory.getLogger(Fighting::class)

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean =
        entity.asElementEntity { attackNearbyEnemies(it, context) }
            .orElseGet { false }

    fun attackNearbyEnemies(attacker: ElementEntity, context: GameContext): Boolean =
        attacker
            .visibleEntities()
            // TODO also check if it is in attack range
            .filterElements { it.isEnemyFor(attacker) }
            .firstOrNull()
            ?.let { enemyToAttack ->
                log.debug("${attacker.callsign} attacks ${enemyToAttack.callsign}")
                attacker.attack(enemyToAttack, context.random)
                true
            }
            ?: false

    private fun ElementEntity.isEnemyFor(other: ElementEntity): Boolean =
        isAbleToFight && affiliationTo(other) == Affiliation.Hostile

}