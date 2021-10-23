package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.DefenseAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.model.combat.defense.AwarenessState
import de.gleex.pltcmd.model.combat.defense.MovementState
import de.gleex.pltcmd.model.combat.defense.TotalDefense
import de.gleex.pltcmd.model.combat.defense.cover
import de.gleex.pltcmd.model.world.WorldMap
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

private val log = KotlinLogging.logger {}

/**
 * Updates the defensive state of a [CombatantEntity].
 */
internal object Defending : BaseBehavior<GameContext>(DefenseAttribute::class, PositionAttribute::class) {

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return entity.asCombatantEntity {
            it.updateDefense(context.world);
            true
        }.orElse(false)
    }

    /**
     * Updates the defense attribute for the current state of this combatant.
     * @param worldMap the world in which this entity is positioned
     **/
    fun CombatantEntity.updateDefense(worldMap: WorldMap) {
        currentDefense = determineDefense(worldMap)
        log.trace { "Defense of $logIdentifier set to $currentDefense" }
    }

    fun CombatantEntity.determineDefense(worldMap: WorldMap): TotalDefense {
        val moving = when {
            asMovableEntity { it.isMoving }.orElse(false) -> MovementState.MOVING
            else                                          -> MovementState.STATIONARY
        }
        val cover = worldMap[currentPosition].cover
        val awareness = AwarenessState.OBSERVING
        return TotalDefense(moving, cover, awareness)
    }
}