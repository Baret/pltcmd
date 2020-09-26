package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.HealthAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.model.elements.Affiliation
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Attacks nearby enemies. */
internal object Fighting : BaseBehavior<GameContext>(HealthAttribute::class, ShootersAttribute::class, PositionAttribute::class, ElementAttribute::class) {

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
        val currentPosition = attacker.currentPosition
        val enemyToAttack = currentPosition.neighbors()
                .map(context::findElementAt)
                .filter { it.isEnemy() }
                .map(Maybe<ElementEntity>::get)
                .firstOrNull()
        if (enemyToAttack != null) {
            log.info("${attacker.callsign} attacks ${enemyToAttack.callsign}")
            attacker.attack(enemyToAttack, context.random)
        }
    }

    private fun Maybe<ElementEntity>.isEnemy(): Boolean =
            filter { entity -> entity.affiliation == Affiliation.Hostile && entity.isAlive }.isPresent

}