package faction

import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.faction.FactionRelations
import de.gleex.pltcmd.model.faction.graph.AffiliationEdge
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerContext
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jgrapht.Graph
import org.jgrapht.GraphTests
import org.jgrapht.graph.builder.GraphTypeBuilder

class FactionRelationsTest : FreeSpec({

    afterSpec {
        FactionRelations.reset()
    }

    val f1 = Faction("faction 1")
    val f2 = Faction("faction 2")
    val f3 = Faction("faction 11")

    "Default relations should be self/neutral" {
        FactionRelations[f1, f1] shouldBe Affiliation.Self
        FactionRelations[f2, f2] shouldBe Affiliation.Self
        FactionRelations[f3, f3] shouldBe Affiliation.Self

        FactionRelations[f1, f2] shouldBe Affiliation.Neutral
        FactionRelations[f1, f3] shouldBe Affiliation.Neutral

        FactionRelations[f2, f3] shouldBe Affiliation.Neutral
    }

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

        shouldThrow<IllegalArgumentException> { FactionRelations[f1, f2] }
    }

    "The relations graph should be 'complete' with self loops" - {
        FactionRelations.reset()
        "with 0 factions" - {
            checkCompleteness(0, FactionRelations.relations)
        }
        repeat(10) {
            Faction("faction $it")
            val numberOfFactions = it + 1
            "with $numberOfFactions factions" - {
                checkCompleteness(numberOfFactions, FactionRelations.relations)
            }
        }
    }
})

private suspend fun FreeSpecContainerContext.checkCompleteness(
    numberOfFactions: Int,
    graph: Graph<Faction, AffiliationEdge>
) {
    val vCount = numberOfFactions
    "Vertex count $vCount" - {
        graph.vertexSet() shouldHaveSize vCount
    }
    val eCount = ((numberOfFactions * (numberOfFactions - 1) / 2)) + numberOfFactions
    "Edge count $eCount" - {
        graph.edgeSet() shouldHaveSize eCount
    }
    "hasSelfLoops" - {
        GraphTests.hasSelfLoops(graph) shouldBe (numberOfFactions > 0)
    }
    "hasMultipleEdges" - {
        GraphTests.hasMultipleEdges(graph) shouldBe false
    }
    "isConnected" - {
        GraphTests.isConnected(graph) shouldBe (numberOfFactions > 0)
    }
    "requireUndirected" - {
        GraphTests.requireUndirected(graph)
    }

    val noSelfLoops: Graph<Faction, AffiliationEdge> =
        GraphTypeBuilder
            .forGraph(graph)
            .allowingSelfLoops(false)
            .buildGraph()
    "complete, when self loops removed" - {
        GraphTests.isComplete(noSelfLoops) shouldBe true
    }
}
