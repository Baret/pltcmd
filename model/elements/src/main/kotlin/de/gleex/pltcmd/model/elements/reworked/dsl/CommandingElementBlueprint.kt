package de.gleex.pltcmd.model.elements.reworked.dsl

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.*
import de.gleex.pltcmd.model.elements.reworked.units.Unit

class CommandingElementBlueprint(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung,
        private val units: Set<Unit>,
        private val subordinates: Set<Element>
) : Blueprint<CommandingElement> {

    override fun new() = CommandingElement(corps, kind, rung, CallSign("WIP"), units, subordinates)

    operator fun times(i: Int) = List(i) { new() }.toSet()
}

operator fun Int.times(blueprint: CommandingElementBlueprint) = blueprint * this