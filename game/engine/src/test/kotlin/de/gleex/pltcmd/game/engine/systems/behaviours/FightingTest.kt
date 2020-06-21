package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.affiliation
import de.gleex.pltcmd.game.engine.entities.types.combatStats
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.combat.CombatStats
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.cobalt.datatypes.Maybe

class FightingTest : StringSpec({
    // enable mocking of extensions
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.CombatantKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.PositionableKt")

    "attackNearbyEnemies with single enemy" {
        val attackerStats = CombatStats()
        val targetStats = CombatStats()
        val attackerPosition = Coordinate(123, 456)
        val (attacker, context) = setupCombat(attackerPosition, attackerStats, targetStats)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 80, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 60, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 40, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 20, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 0, false)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, -20, false)
    }
})

private fun setupCombat(attackerPosition: Coordinate, attackerStats: CombatStats, targetStats: CombatStats): Pair<ElementEntity, GameContext> {
    val targetPosition = attackerPosition.neighbors()
            .last()

    val attacker = mockk<ElementEntity>()
    every { attacker.combatStats } returns attackerStats
    every { attacker.currentPosition } returns attackerPosition
    val target = mockk<ElementEntity>()
    every { target.combatStats } returns targetStats
    every { target.affiliation } returns Affiliation.Hostile

    val context = mockk<GameContext>()
    every { context.findElementAt(any()) } returns Maybe.empty()
    every { context.findElementAt(targetPosition) } returns Maybe.of(target)
    return Pair(attacker, context)
}

private fun assertCombatResult(attackerStats: CombatStats, targetStats: CombatStats, expectedHealth: Int, expectedAlive: Boolean = true) {
    attackerStats.health.value shouldBe 100
    targetStats.health.value shouldBe expectedHealth
    targetStats.isAlive.value shouldBe expectedAlive
}
