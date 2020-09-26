package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HealthAttributeTest : StringSpec({

    "wound must reduce the healthy units" {
        val underTest = createUnderTest()
        underTest.wound(3)
        underTest.healthy shouldBe 97

        underTest.wound(0)
        underTest.healthy shouldBe 97

        underTest.wound(-5)
        underTest.healthy shouldBe 97

        underTest.wound(105)
        underTest.healthy shouldBe 0
    }

    "treatWounded must restore wounded units" {
        val underTest = createUnderTest()
        underTest.wound(60)
        underTest.healthy shouldBe 40

        underTest.treatWounded(3)
        underTest.healthy shouldBe 43

        underTest.treatWounded(0)
        underTest.healthy shouldBe 43

        underTest.treatWounded(-8)
        underTest.healthy shouldBe 43

        underTest.treatWounded(77)
        underTest.healthy shouldBe 100
    }

    "onDeath must trigger on exact 0 hp" {
        val underTest = createUnderTest()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.wound(99)
        underTest.isAlive shouldBe true
        isDead shouldBe false

        underTest.wound(1)
        underTest.isAlive shouldBe false
        isDead shouldBe true

    }

    "onDeath must trigger on overdamage" {
        val underTest = createUnderTest()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.wound(200)

        underTest.isAlive shouldBe false
        isDead shouldBe true

        // do not trigger again when already dead
        isDead = false
        underTest.wound(1)
        underTest.isAlive shouldBe false
        isDead shouldBe false
    }

    "onDeath should not trigger again when already dead" {
        val underTest = createUnderTest()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.wound(100)

        underTest.isAlive shouldBe false
        isDead shouldBe true

        // do not trigger again when already dead
        isDead = false
        underTest.wound(1)
        underTest.isAlive shouldBe false
        isDead shouldBe false
    }

    "onDeath should trigger multiple callbacks" {
        val underTest = createUnderTest()
        val isDead = arrayOf(false, false, false)
        underTest.onDeath { isDead[0] = true }
        underTest.onDeath { isDead[1] = true }
        underTest.onDeath { isDead[2] = true }

        underTest.wound(100)

        underTest.isAlive shouldBe false
        isDead[0] shouldBe true
        isDead[1] shouldBe true
        isDead[2] shouldBe true
    }

})

private fun createUnderTest(): HealthAttribute {
    val units = (Units.Rifleman * 100).new()
    println("created for test: $units")
    return HealthAttribute(units)
}

