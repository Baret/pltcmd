package de.gleex.pltcmd.util.knowledge

/*
class KnownByGradeTest : WordSpec({

    val knownBit = "testing"
    val unrevealed = KnownByGrade<String, KnownByGrade<String, *>>(knownBit, KnowledgeGrade.NONE)
    val halfRevealed = KnownByGrade<String, KnownByGrade<String, *>>(knownBit, KnowledgeGrade.MEDIUM)
    val revealed = KnownByGrade<String, KnownByGrade<String, *>>(knownBit, KnowledgeGrade.FULL)
    val otherRevealed = KnownByGrade<String, KnownByGrade<String, *>>("other", KnowledgeGrade.FULL)

    "revealed" should {
        "be NONE if not revealed" {
            unrevealed.revealed shouldBe KnowledgeGrade.NONE
        }
        "be MEDIUM if partly revealed" {
            halfRevealed.revealed shouldBe KnowledgeGrade.MEDIUM
        }
        "be FULL if revealed" {
            revealed.revealed shouldBe KnowledgeGrade.FULL
        }
    }

    "reveal" should {
        unrevealed.revealed shouldBe KnowledgeGrade.NONE
        unrevealed.revealAt(KnowledgeGrade.FULL) { it } shouldBe Maybe.empty()

        unrevealed.reveal(KnowledgeGrade.FULL)
        // unrevealed is now revealed!
        "change revealed to true" {
            unrevealed.revealed shouldBe KnowledgeGrade.FULL
        }
        "change bit to origin" {
            unrevealed.revealAt(KnowledgeGrade.FULL) { it } shouldBe Maybe.of(knownBit)
        }
    }

    "mergeWith" should {
        "extend unrevealed for same origin" {
            unrevealed.revealed shouldBe KnowledgeGrade.NONE

            val merged = unrevealed.mergeWith(revealed)

            merged shouldBe true
            unrevealed shouldBe revealed
            unrevealed.revealed shouldBe KnowledgeGrade.FULL
        }
        "stay unrevealed for different origin" {
            unrevealed.revealed shouldBe KnowledgeGrade.NONE
            revealed.revealAt(KnowledgeGrade.FULL) { it } shouldNotBe otherRevealed.revealAt(KnowledgeGrade.FULL) { it }

            val merged = unrevealed.mergeWith(otherRevealed)

            merged shouldBe false
            unrevealed.revealed shouldBe KnowledgeGrade.NONE
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
*/