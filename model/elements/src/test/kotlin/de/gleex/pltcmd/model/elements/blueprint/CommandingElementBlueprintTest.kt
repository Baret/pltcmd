package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.scopes.ShouldSpecContainerScope
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf

class CommandingElementBlueprintTest : ShouldSpec() {
    init {
        context("The default element blueprint for a rifle platoon should contain the correct sub-blueprints and no actual elements/units.") {
            val platoonBlueprint = Elements.riflePlatoon
            platoonBlueprint should beInstanceOf<CommandingElementBlueprint>()
            assertUnits(platoonBlueprint)
            context("Platoon") {
                should("should only contain ${CommandingElementBlueprint::class.simpleName}s") {
                    platoonBlueprint.subordinates.forAll {
                        it should beInstanceOf<CommandingElementBlueprint>()
                    }
                    platoonBlueprint.subordinates.forNone {
                        it should beInstanceOf<Element>()
                        it should beInstanceOf<ElementBlueprint>()
                    }
                }
            }

            platoonBlueprint
                    .subordinates
                    .forEachIndexed { i, squad ->
                        context("Each squad ($i)") {
                            squad as CommandingElementBlueprint
                            assertUnits(squad)
                            should("should only contain ${ElementBlueprint::class.simpleName}s") {
                                squad.subordinates.forAll {
                                    it should beInstanceOf<ElementBlueprint>()
                                }
                                squad.subordinates.forNone {
                                    it should beInstanceOf<Element>()
                                    it should beInstanceOf<CommandingElementBlueprint>()
                                }
                            }

                            squad.subordinates
                                    .forEachIndexed { i, fireteam ->
                                        context("Fireteam ($i)") {
                                            assertUnits(fireteam)
                                        }
                                    }
                        }
                    }
        }
    }

    private suspend fun ShouldSpecContainerScope.assertUnits(commandingElementBlueprint: AbstractElementBlueprint<*>) {
        should("should only contain unit blueprints, no units") {
            commandingElementBlueprint.units.forAll {
                it should beInstanceOf<Units>()
            }
            commandingElementBlueprint.units.forNone {
                it should beInstanceOf<Unit>()
            }
        }
    }

}