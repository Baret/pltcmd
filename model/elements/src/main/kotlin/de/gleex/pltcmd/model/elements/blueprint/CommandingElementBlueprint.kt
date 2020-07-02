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
        /**
         * Visible for testing.
         */
        internal val units: List<Units>,
        /**
         * Visible for testing.
         */
        internal val subordinates: List<Blueprint<Element>>
) : Blueprint<CommandingElement> {

    override fun new() = CommandingElement(corps, kind, rung, units.new(), subordinates.new())

    internal infix fun commanding(subordinates: List<Blueprint<Element>>) = copy(
            corps = corps,
            kind = kind,
            rung = rung,
            units = units,
            subordinates = subordinates + this.subordinates
    )

    operator fun times(i: Int) = List(i) { copy() }

    operator fun plus(list: List<Blueprint<Element>>): List<Blueprint<Element>> = list + this
}

internal operator fun Int.times(blueprint: CommandingElementBlueprint) = blueprint * this