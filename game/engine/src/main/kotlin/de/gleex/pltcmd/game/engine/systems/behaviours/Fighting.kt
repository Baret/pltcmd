package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.model.elements.Affiliation
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Attacks nearby enemies. */
internal object Fighting : BaseBehavior<GameContext>(ShootersAttribute::class, PositionAttribute::class, ElementAttribute::class) {

    private val log = LoggerFactory.getLogger(Fighting::class)

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is ElementType) {
            return false
        }
        @Suppress("UNCHECKED_CAST")
        attackNearbyEnemies(entity as ElementEntity, context)
        return true
    }

    fun attackNearbyEnemies(attacker: ElementEntity, context: GameContext) {
        attacker
                .visibleEntities()
                // TODO also check if it is in attack range
                .filterElements { it.isEnemy() }
                .firstOrNull()
                ?.let { enemyToAttack ->
                    log.info("${attacker.callsign} attacks ${enemyToAttack.callsign}")
                    attacker.attack(enemyToAttack, context.random)
                }
    }

    private fun ElementEntity.isEnemy(): Boolean =
            isAbleToFight && affiliation == Affiliation.Hostile

}