package de.gleex.pltcmd.util.knowledge

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class KnownByBooleanTest : WordSpec({

    val knownBit = "testing"
    val unrevealed = KnownByBoolean<String, KnownByBoolean<String, *>>(knownBit, false)
    val revealed = KnownByBoolean<String, KnownByBoolean<String, *>>(knownBit, true)
    val otherRevealed = KnownByBoolean<String, KnownByBoolean<String, *>>("other", true)

    "bit" should {
        "be null if not revealed" {
            unrevealed.bit shouldBe null
        }
        "be the value if not revealed" {
            revealed.bit shouldBe knownBit
        }
    }

    "reveal" should {
        unrevealed.revealed shouldBe false
        unrevealed.bit shouldBe null

        unrevealed.reveal()
        // unrevealed is now revealed!
        "change revealed to true" {
            unrevealed.revealed shouldBe true
        }
        "change bit to origin" {
            unrevealed.bit shouldBe knownBit
        }
    }

    "mergeWith" should {
        "extend unrevealed for same origin" {
            unrevealed.revealed shouldBe false

            val merged = unrevealed.mergeWith(revealed)

            merged shouldBe revealed
            unrevealed.revealed shouldBe true
        }
        "stay unrevealed for different origin" {
            unrevealed.revealed shouldBe false
            revealed.bit shouldNotBe otherRevealed.bit

            val merged = unrevealed.mergeWith(otherRevealed)

            merged shouldBe unrevealed
            unrevealed.revealed shouldBe false
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
