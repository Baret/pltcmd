package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HealthAttributeTest : StringSpec({

    "wound must reduce the combat-ready units" {
        val underTest = createUnderTest()
        underTest.wound(3)
        underTest.combatReady shouldBe 97

        underTest.wound(0)
        underTest.combatReady shouldBe 97

        underTest.wound(-5)
        underTest.combatReady shouldBe 97

        underTest.wound(105)
        underTest.combatReady shouldBe 0
    }

    "treatWounded must restore wounded units" {
        val underTest = createUnderTest()
        underTest.wound(60)
        underTest.combatReady shouldBe 40

        underTest.treatWounded(3)
        underTest.combatReady shouldBe 43

        underTest.treatWounded(0)
        underTest.combatReady shouldBe 43

        underTest.treatWounded(-8)
        underTest.combatReady shouldBe 43

        underTest.treatWounded(77)
        underTest.combatReady shouldBe 100
    }

    "onDefeat must trigger on exact 0 combat-ready" {
        val underTest = createUnderTest()
        var isDead = false
        underTest.onDefeat { isDead = true }

        underTest.wound(99)
        underTest.isAbleToFight shouldBe true
        isDead shouldBe false

        underTest.wound(1)
        underTest.isAbleToFight shouldBe false
        isDead shouldBe true

    }

    "onDefeat must trigger on overdamage" {
        val underTest = createUnderTest()
        var isDefeated = false
        underTest.onDefeat { isDefeated = true }

        underTest.wound(200)

        underTest.isAbleToFight shouldBe false
        isDefeated shouldBe true

        // do not trigger again when already dead
        isDefeated = false
        underTest.wound(1)
        underTest.isAbleToFight shouldBe false
        isDefeated shouldBe false
    }

    "onDefeat should not trigger again when already defeated" {
        val underTest = createUnderTest()
        var isDefeated = false
        underTest.onDefeat { isDefeated = true }

        underTest.wound(100)

        underTest.isAbleToFight shouldBe false
        isDefeated shouldBe true

        // do not trigger again when already dead
        isDefeated = false
        underTest.wound(1)
        underTest.isAbleToFight shouldBe false
        isDefeated shouldBe false
    }

    "onDefeat should trigger multiple callbacks" {
        val underTest = createUnderTest()
        val isDefeated = arrayOf(false, false, false)
        underTest.onDefeat { isDefeated[0] = true }
        underTest.onDefeat { isDefeated[1] = true }
        underTest.onDefeat { isDefeated[2] = true }

        underTest.wound(100)

        underTest.isAbleToFight shouldBe false
        isDefeated[0] shouldBe true
        isDefeated[1] shouldBe true
        isDefeated[2] shouldBe true
    }

})

private fun createUnderTest(): HealthAttribute {
    val units = (Units.Rifleman * 100).new()
    println("created for test: $units")
    return HealthAttribute(units)
}

