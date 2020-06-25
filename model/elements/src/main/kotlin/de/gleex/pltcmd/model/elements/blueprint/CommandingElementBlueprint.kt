package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new

/**
 * A blueprint to create new [CommandingElement]s.
 */
data class CommandingElementBlueprint(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung,
        private val units: List<Units>,
        private val subordinates: List<Blueprint<Element>>
) : Blueprint<CommandingElement> {

    override fun new() = CommandingElement(corps, kind, rung, units.new(), subordinates.new())

    operator fun times(i: Int) = List(i) { copy() }
}

operator fun Int.times(blueprint: CommandingElementBlueprint) = blueprint * this