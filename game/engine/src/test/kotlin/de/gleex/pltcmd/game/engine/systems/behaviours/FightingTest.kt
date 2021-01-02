package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ContactsAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import kotlin.random.Random

class FightingTest : StringSpec({
    // enable mocking of extensions
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.CombatantKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt")
    mockkStatic("de.gleex.pltcmd.game.engine.entities.types.PositionableKt")

    "attackNearbyEnemies with single attacker against single enemy" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly)
        val context = createContext()
        val target = createTarget(attacker, context)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)

        // no more damage on already dead combatants
        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)
    }


    "attackNearbyEnemies with single attacker against multiple single enemy soldiers" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly)
        val context = createContext()
        val (target1, target2, target3) = createTargets(attacker, context, createInfantryElement(), createInfantryElement(), createInfantryElement())
        assertCombatResult(attacker, target1, 1, true)
        assertCombatResult(attacker, target2, 1, true)
        assertCombatResult(attacker, target3, 1, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 1, true)
        assertCombatResult(attacker, target3, 1, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 0, false)
        assertCombatResult(attacker, target3, 1, true)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target1, 0, false)
        assertCombatResult(attacker, target2, 0, false)
        assertCombatResult(attacker, target3, 0, false)
    }

    "attackNearbyEnemies with multiple shooters and single enemy with multiple soldiers" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly, Elements.rifleSquad.new())
        val context = createContext()
        val target = createTarget(attacker, context, createInfantryElement((Units.Rifleman * 100).new()))

        Fighting.attackNearbyEnemies(attacker, context) // 38 dmg
        assertCombatResult(attacker, target, 62, true)

        Fighting.attackNearbyEnemies(attacker, context) // 40 dmg
        assertCombatResult(attacker, target, 22, true)

        Fighting.attackNearbyEnemies(attacker, context) // 45 dmg
        assertCombatResult(attacker, target, 0, false)
    }

    "attackNearbyEnemies with multiple wounded shooters and single enemy with multiple soldiers" {
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, Affiliation.Friendly, Elements.rifleSquad.new())
        val context = createContext()
        val target = createTarget(attacker, context, createInfantryElement((Units.Rifleman * 100).new()))
        val singleRifleman = createCombatant(attackerPosition.movedBy(2,2), Affiliation.Hostile)
        singleRifleman.attack(attacker, context.random)
        val attacksAbleToFight = 4
        attacker.combatReadyCount shouldBe attacksAbleToFight
        attacker.woundedCount shouldBe 6

        var expectedTargetCombatReady = target.combatReadyCount
        forAll( // shots random hits
                row(21),
                row(18),
                row(16),
                row(24),
                row(19)
        ) { expectedDamage ->
            Fighting.attackNearbyEnemies(attacker, context)
            expectedTargetCombatReady -= expectedDamage
            assertCombatResult(attacker, target, expectedTargetCombatReady, true, attacksAbleToFight)
        }
        expectedTargetCombatReady shouldBe 2

        Fighting.attackNearbyEnemies(attacker, context) // 24 dmg
        assertCombatResult(attacker, target, 0, false, attacksAbleToFight)
    }
})

private fun createContext(): GameContext {

    val context = mockk<GameContext>()
    every { context.random } returns Random(123L)
    every { context.elementsAt(any()) } returns EntitySet()

    return context
}

fun createTarget(attacker: ElementEntity, context: GameContext, element: CommandingElement = createInfantryElement()): ElementEntity =
        createTargets(attacker, context, element).first()

fun createTargets(attacker: ElementEntity, context: GameContext, vararg elements: CommandingElement): List<ElementEntity> {
    val attackerPosition = attacker.currentPosition
    val neighbors = attackerPosition.neighbors()
    return elements.mapIndexed { index, element ->
        val neighborPosition = neighbors[index]
        // position further away for ranged combat at 300 m
        val offsetFromAttacker = (neighborPosition - attackerPosition)
        val targetPosition = neighborPosition.movedBy(offsetFromAttacker.eastingFromLeft * 2, offsetFromAttacker.northingFromBottom * 2)
        val target = createCombatant(targetPosition, Affiliation.Hostile, element)
        attacker.rememberContact(target)
        return@mapIndexed target
    }
}

fun createCombatant(position: Coordinate, affiliation: Affiliation, element: CommandingElement = createInfantryElement()): ElementEntity {
    return newEntityOfType(ElementType) {
        attributes(
                ElementAttribute(element, affiliation),
                PositionAttribute(position.toProperty()),
                ShootersAttribute(element),
                ContactsAttribute()
        )
        behaviors(Fighting)
        facets()
    }
}

private fun createInfantryElement(units: Set<Unit> = setOf(Units.Rifleman.new())) =
        CommandingElement(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, units)

private fun assertCombatResult(attackerStats: ElementEntity, targetStats: CombatantEntity, expectedCombatReady: Int, expectedAlive: Boolean = true, expectedAttackerCombatReady: Int = attackerStats.element.totalUnits) {
    assertSoftly {
        attackerStats.combatReadyCount shouldBe expectedAttackerCombatReady
        targetStats.combatReadyCount shouldBe expectedCombatReady
        targetStats.isAbleToFight shouldBe expectedAlive
    }
}
