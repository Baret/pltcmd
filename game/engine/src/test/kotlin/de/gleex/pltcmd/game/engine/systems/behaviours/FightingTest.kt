package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.SightedAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.DefenseAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.systems.behaviours.Defending.updateDefense
import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.faction.FactionRelations
import de.gleex.pltcmd.model.signals.vision.Visibility
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainType
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

    val playerFaction = Faction("player faction")
    val opfor = Faction("opposing force")
    FactionRelations[playerFaction, opfor] = Affiliation.Hostile

    "attackNearbyEnemies with single attacker against single enemy" {
        val context = createContext()
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, playerFaction, context)
        val target = createTarget(attacker, opfor, context)

        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)

        // no more damage on already dead combatants
        Fighting.attackNearbyEnemies(attacker, context)
        assertCombatResult(attacker, target, 0, false)
    }


    "attackNearbyEnemies with single attacker against multiple single enemy soldiers" {
        val context = createContext()
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, playerFaction, context)
        val (target1, target2, target3) = createTargets(
            attacker, opfor, context,
            createInfantryElement(),
            createInfantryElement(),
            createInfantryElement(),
        )
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
        val context = createContext()
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, playerFaction, context, Elements.rifleSquad.new())
        val target = createTarget(attacker, opfor, context, createInfantryElement((Units.Rifleman * 100).new()))

        Fighting.attackNearbyEnemies(attacker, context) // 20 dmg
        assertCombatResult(attacker, target, 80, true)

        Fighting.attackNearbyEnemies(attacker, context) // 23 dmg
        assertCombatResult(attacker, target, 57, true)

        Fighting.attackNearbyEnemies(attacker, context) // 21 dmg
        assertCombatResult(attacker, target, 36, true)

        Fighting.attackNearbyEnemies(attacker, context) // 21 dmg
        assertCombatResult(attacker, target, 15, true)

        Fighting.attackNearbyEnemies(attacker, context) // 22 dmg
        assertCombatResult(attacker, target, 0, false)
    }

    "attackNearbyEnemies with multiple wounded shooters and single enemy with multiple soldiers" {
        val context = createContext()
        val attackerPosition = Coordinate(123, 456)
        val attacker = createCombatant(attackerPosition, playerFaction, context, Elements.rifleSquad.new())
        val target = createTarget(attacker, opfor, context, createInfantryElement((Units.Rifleman * 100).new()))
        val singleRifleman = createCombatant(attackerPosition.movedBy(2, 2), opfor, context)
        singleRifleman.attack(attacker, context.random)
        val attackersAbleToFight = 7
        attacker.combatReadyCount shouldBe attackersAbleToFight
        attacker.woundedCount shouldBe 3

        var expectedTargetCombatReady = target.combatReadyCount
        forAll( // shots random hits
            row(19),
            row(21),
            row(21),
            row(13),
            row(19)
        ) { expectedDamage ->
            Fighting.attackNearbyEnemies(attacker, context)
            expectedTargetCombatReady -= expectedDamage
            assertCombatResult(attacker, target, expectedTargetCombatReady, true, attackersAbleToFight)
        }
        expectedTargetCombatReady shouldBe 7

        Fighting.attackNearbyEnemies(attacker, context) // 13 dmg
        assertCombatResult(attacker, target, 0, false, attackersAbleToFight)
    }
})

private fun createContext(): GameContext {

    val context = mockk<GameContext>()
    val random = Random(123L)
    every { context.random } returns random
    every { context.elementsAt(any()) } returns EntitySet()
    every { context.world[any<Coordinate>()].type } returns TerrainType.FOREST

    return context
}

fun createTarget(
    attacker: ElementEntity,
    opfor: Faction,
    context: GameContext,
    element: CommandingElement = createInfantryElement()
): ElementEntity =
    createTargets(attacker, opfor, context, element).first()

fun createTargets(
    attacker: ElementEntity,
    opfor: Faction,
    context: GameContext,
    vararg elements: CommandingElement
): List<ElementEntity> {
    val attackerPosition = attacker.currentPosition
    val neighbors = attackerPosition.neighbors()
    return elements.mapIndexed { index, element ->
        val neighborPosition = neighbors[index]
        // position further away for ranged combat at 300 m
        val offsetFromAttacker = (neighborPosition - attackerPosition)
        val targetPosition =
            neighborPosition.movedBy(offsetFromAttacker.eastingFromLeft * 2, offsetFromAttacker.northingFromBottom * 2)
        val target = createCombatant(targetPosition, opfor, context, element)
        attacker.sighted(target, Visibility.GOOD)
        return@mapIndexed target
    }
}

fun createCombatant(
    position: Coordinate,
    faction: Faction,
    context: GameContext,
    element: CommandingElement = createInfantryElement()
): ElementEntity {
    val combatant: ElementEntity = newEntityOfType(ElementType) {
        attributes(
            ElementAttribute(element),
            FactionAttribute(faction),
            PositionAttribute(position.toProperty()),
            ShootersAttribute(element),
            DefenseAttribute(),
            SightedAttribute(),
            MovementBaseSpeed(element)
        )
        behaviors(Defending, Fighting)
        facets()
    }
    combatant.updateDefense(context.world)
    return combatant
}

private fun createInfantryElement(units: Set<Unit> = setOf(Units.Rifleman.new())) =
    CommandingElement(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, units)

private fun assertCombatResult(
    attackerStats: ElementEntity,
    targetStats: CombatantEntity,
    expectedCombatReady: Int,
    expectedAlive: Boolean = true,
    expectedAttackerCombatReady: Int = attackerStats.element.totalUnits
) {
    assertSoftly {
        attackerStats.combatReadyCount shouldBe expectedAttackerCombatReady
        targetStats.combatReadyCount shouldBe expectedCombatReady
        targetStats.isAbleToFight shouldBe expectedAlive
    }
}
