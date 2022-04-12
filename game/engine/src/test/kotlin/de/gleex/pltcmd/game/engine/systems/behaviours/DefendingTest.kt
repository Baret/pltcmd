package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.movementPath
import de.gleex.pltcmd.game.engine.systems.behaviours.Defending.determineDefense
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.mockk.every

class DefendingTest : StringSpec({
    // create new combatant for each test
    isolationMode = IsolationMode.InstancePerTest

    val playerFaction = Faction("player faction")
    val context = createContext()
    val combatantPosition = Coordinate(123, 456)
    val combatant = createCombatant(combatantPosition, playerFaction, context)

    "determineDefense standing in forest" {
        testedDefense(combatant, context) shouldBe (0.3 plusOrMinus 0.0000000000000001)
    }

    "determineDefense moving in forest" {
        movingNorth(combatant)
        testedDefense(combatant, context) shouldBe 0.5
    }

    "determineDefense standing in grassland" {
        beInGrassland(combatantPosition, context)
        testedDefense(combatant, context) shouldBe 0.1
    }

    "determineDefense moving in grassland" {
        movingNorth(combatant)
        beInGrassland(combatantPosition, context)
        testedDefense(combatant, context) shouldBe (0.3 plusOrMinus 0.0000000000000001)
    }
})

private fun testedDefense(
    combatant: ElementEntity,
    context: GameContext
) = combatant.determineDefense(context.world).attackReduction

fun movingNorth(
    combatant: ElementEntity
) {
    combatant.movementPath.apply {
        add(combatant.currentPosition.withRelativeNorthing(1))
    }
}

fun beInGrassland(
    combatantPosition: Coordinate,
    context: GameContext
) {
    every { context.world[combatantPosition].terrain } returns Terrain.Companion.of(TerrainType.GRASSLAND, TerrainHeight.MIN)
}
