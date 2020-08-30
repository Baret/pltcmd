package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.combat.CombatStats
import de.gleex.pltcmd.model.elements.combat.Weapons
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.random.Random

class FightingTest : StringSpec({
    // enable mocking of extensions
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.CombatantKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.PositionableKt")

    val singleRifle = listOf(Weapons.assaultRifle).toProperty()
    "attackNearbyEnemies with single enemy" {
        val attackerStats = CombatStats(singleRifle)
        val targetStats = CombatStats(singleRifle)
        val attackerPosition = Coordinate(123, 456)
        val (attacker, context) = setupCombat(attackerPosition, attackerStats, targetStats)

        var expectedHealth = targetStats.health.value
        listOf(7, 5, 7, 6, 4, 8, 3, 8, 7, 5, 8, 6, 6, 6, 8, 5).forEach { expectedDamage ->
            Fighting.attackNearbyEnemies(attacker, context)
            expectedHealth -= expectedDamage
            assertCombatResult(attackerStats, targetStats, expectedHealth, true)
        }
        expectedHealth shouldBe 1

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 0, false)

        // no more damage on already dead combatants
        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attackerStats, targetStats, 0, false)
    }


    "attackNearbyEnemies with multiple enemies" {
        val attackerStats = CombatStats(singleRifle)
        val target1Stats = CombatStats(singleRifle)
        val target2Stats = CombatStats(singleRifle)
        val target3Stats = CombatStats(singleRifle)
        val attackerPosition = Coordinate(123, 456)
        val (attacker, context) = setupCombat(attackerPosition, attackerStats, target1Stats, target2Stats, target3Stats)
        assertCombatResult(attackerStats, target1Stats, 100, true)
        assertCombatResult(attackerStats, target2Stats, 100, true)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(17) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 100, true)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(17) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 0, false)
        assertCombatResult(attackerStats, target3Stats, 100, true)

        repeat(18) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attackerStats, target1Stats, 0, false)
        assertCombatResult(attackerStats, target2Stats, 0, false)
        assertCombatResult(attackerStats, target3Stats, 0, false)
    }
})

private fun setupCombat(attackerPosition: Coordinate, attackerStats: CombatStats, vararg targetStats: CombatStats): Pair<ElementEntity, GameContext> {

    val context = mockk<GameContext>()
    every { context.random } returns Random(123L)
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
        targetStats.isAlive shouldBe expectedAlive
    }
}
