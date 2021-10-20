package faction

import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.faction.FactionRelations
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FactionRelationsTest : StringSpec({

    beforeTest {
        FactionRelations.reset()
    }

    val f1 = Faction("faction 1")
    val f2 = Faction("faction 2")
    val f3 = Faction("faction 11")

    "relation should be stored and accessible in any order" {
        FactionRelations[f1, f2] = Affiliation.Friendly

        FactionRelations[f1, f2] shouldBe Affiliation.Friendly
        FactionRelations[f2, f1] shouldBe Affiliation.Friendly
    }

    "set should be cumulative" {
        FactionRelations[f1, f2] = Affiliation.Hostile
        FactionRelations[f2, f1] = Affiliation.Friendly

        FactionRelations[f1, f2] shouldBe Affiliation.Friendly
        FactionRelations[f2, f1] shouldBe Affiliation.Friendly
    }

    "multiple relations must work" {
        FactionRelations[f1, f2] = Affiliation.Hostile
        FactionRelations[f1, f3] = Affiliation.Friendly
        FactionRelations[f2, f3] = Affiliation.Neutral

        FactionRelations[f1, f2] shouldBe Affiliation.Hostile
        FactionRelations[f1, f3] shouldBe Affiliation.Friendly
        FactionRelations[f2, f3] shouldBe Affiliation.Neutral
    }

    "reset should clear relations" {
        FactionRelations[f1, f2] = Affiliation.Hostile

        FactionRelations.reset()

        FactionRelations[f1, f2] shouldBe Affiliation.Unknown
    }

})
