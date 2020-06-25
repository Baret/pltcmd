package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.combat.CombatStats
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.assertions.assertSoftly
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

        // no more damage on already dead combatants
        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 0, false)
    }


    "attackNearbyEnemies with multiple enemies" {
        val attackerStats = CombatStats()
        val target1Stats = CombatStats()
        val target2Stats = CombatStats()
        val target3Stats = CombatStats()
        val attackerPosition = Coordinate(123, 456)
        val (attacker, context) = setupCombat(attackerPosition, attackerStats, target1Stats, target2Stats, target3Stats)
        assertCombatResult(attackerStats, target1Stats, 100, true)
        assertCombatResult(attackerStats, target2Stats, 100, true)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(5) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 100, true)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(5) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 0, false)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(5) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 0, false)
        assertCombatResult(attackerStats, target3Stats, 0, false)
    }
})

private fun setupCombat(attackerPosition: Coordinate, attackerStats: CombatStats, vararg targetStats: CombatStats): Pair<ElementEntity, GameContext> {

    val context = mockk<GameContext>()
    every { context.findElementAt(any()) } returns Maybe.empty()

    val attacker = mockk<ElementEntity>()
    every { attacker.combatStats } returns attackerStats
    every { attacker.currentPosition } returns attackerPosition
    every { attacker.callsign } returns CallSign("attacker")

    val neighbors = attackerPosition.neighbors()
    targetStats.forEachIndexed { index, targetStat ->
        val target = mockk<ElementEntity>()
        every { target.combatStats } returns targetStat
        every { target.affiliation } returns Affiliation.Hostile
        every { target.callsign } returns CallSign("target$index")
        val targetPosition = neighbors[index]
        every { context.findElementAt(targetPosition) } returns Maybe.of(target)
    }
    return Pair(attacker, context)
}

private fun assertCombatResult(attackerStats: CombatStats, targetStats: CombatStats, expectedHealth: Int, expectedAlive: Boolean = true) {
    assertSoftly {
        attackerStats.health.value shouldBe 100
        targetStats.health.value shouldBe expectedHealth
        targetStats.isAlive.value shouldBe expectedAlive
    }
}
