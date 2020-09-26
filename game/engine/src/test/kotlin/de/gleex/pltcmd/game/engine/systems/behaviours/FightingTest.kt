package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.HealthAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.random.Random

class FightingTest : StringSpec({
    // enable mocking of extensions
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.CombatantKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.PositionableKt")

    "attackNearbyEnemies with single enemy" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly)
        val context = createContext()
        val target = createTarget(attackerPosition, context, Affiliation.Hostile)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 1, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)

        // no more damage on already dead combatants
        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)
    }


    "attackNearbyEnemies with multiple enemies" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly)
        val context = createContext()
        val (target1, target2, target3) = createTargets(attackerPosition, context, Affiliation.Hostile, Affiliation.Hostile, Affiliation.Hostile)
        assertCombatResult(attacker, target1, 100, true)
        assertCombatResult(attacker, target2, 100, true)
        assertCombatResult(attacker, target3, 100, true)

        repeat(17) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 100, true)
        assertCombatResult(attacker, target3, 100, true)

        repeat(17) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 0, false)
        assertCombatResult(attacker, target3, 100, true)

        repeat(18) { Fighting.attackNearbyEnemies(attacker, context) }
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 0, false)
        assertCombatResult(attacker, target3, 0, false)
    }

    "attackNearbyEnemies with multiple shooters" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly, Elements.rifleSquad.new())
        val context = createContext()
        val target = createTarget(attackerPosition, context, Affiliation.Hostile)

        Fighting.attackNearbyEnemies(attacker, context) // 49 dmg
        assertCombatResult(attacker, target, 51, true)

        Fighting.attackNearbyEnemies(attacker, context) // 53 dmg
        assertCombatResult(attacker, target, 0, false)
    }
})

private fun createContext(): GameContext {

    val context = mockk<GameContext>()
    every { context.random } returns Random(123L)
    every { context.findElementAt(any()) } returns Maybe.empty()

    return context
}

fun createTarget(attackerPosition: Coordinate, context: GameContext, affiliation: Affiliation): ElementEntity =
        createTargets(attackerPosition, context, affiliation).first()

fun createTargets(attackerPosition: Coordinate, context: GameContext, vararg affiliations: Affiliation): List<ElementEntity> {
    val neighbors = attackerPosition.neighbors()
    return affiliations.mapIndexed { index, affiliation ->
        val targetPosition = neighbors[index]
        val target = createCombatant(targetPosition, affiliation)
        every { context.findElementAt(targetPosition) } returns Maybe.of(target)
        return@mapIndexed target
    }
}


fun createCombatant(position: Coordinate, affiliation: Affiliation, element: CommandingElement = createRiflemanElement()): ElementEntity {
    return newEntityOfType(ElementType) {
        attributes(
                ElementAttribute(element, affiliation),
                PositionAttribute(position.toProperty()),
                HealthAttribute(element),
                ShootersAttribute(element)
        )
        behaviors(Fighting)
        facets()
    }
}

private fun createRiflemanElement(units: Set<Unit> = setOf(Units.Rifleman.new())) =
        CommandingElement(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, units)

private fun assertCombatResult(
        attackerStats: ElementEntity,
        targetStats: CombatantEntity,
        expectedHealth: Int,
        expectedAlive: Boolean = true,
) {
    assertSoftly {
        attackerStats.health shouldBe attackerStats.element.totalUnits
        targetStats.health shouldBe expectedHealth
        targetStats.isAlive shouldBe expectedAlive
    }
}
