package de.gleex.pltcmd.game.engine.attributes.combat

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HealthAttributeTest : StringSpec({

    "minus must reduce the health" {
        val underTest = HealthAttribute()
        underTest.minus(3)
        underTest.health.value shouldBe 97

        underTest.minus(0)
        underTest.health.value shouldBe 97

        underTest.minus(-5)
        underTest.health.value shouldBe 102

        underTest.minus(105)
        underTest.health.value shouldBe -3
    }

    "plus must increase the health" {
        val underTest = HealthAttribute()
        underTest.plus(3)
        underTest.health.value shouldBe 103

        underTest.plus(0)
        underTest.health.value shouldBe 103

        underTest.plus(-8)
        underTest.health.value shouldBe 95

        underTest.plus(99999905)
        underTest.health.value shouldBe 100000000
    }

    "onDeath must trigger on exact 0 hp" {
        val underTest = HealthAttribute()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.minus(99)
        underTest.isAlive shouldBe true
        isDead shouldBe false

        underTest.minus(1)
        underTest.isAlive shouldBe false
        isDead shouldBe true

    }

    "onDeath must trigger on overdamage" {
        val underTest = HealthAttribute()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.minus(200)

        underTest.isAlive shouldBe false
        isDead shouldBe true

        // do not trigger again when already dead
        isDead = false
        underTest.minus(1)
        underTest.isAlive shouldBe false
        isDead shouldBe false
    }

    "onDeath should not trigger again when already dead" {
        val underTest = HealthAttribute()
        var isDead = false
        underTest.onDeath { isDead = true }

        underTest.minus(100)

        isDead shouldBe true

        // do not trigger again when already dead
        isDead = false
        underTest.minus(1)
        isDead shouldBe false
    }
})
