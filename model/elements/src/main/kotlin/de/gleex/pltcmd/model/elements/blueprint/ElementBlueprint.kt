package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Units

/**
 * A blueprint used to create new instances of [Element].
 *
 * To create instances of [CommandingElement] use a [CommandingElementBlueprint].
 */
data class ElementBlueprint(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung,
        /**
         * Visible for testing.
         */
        internal val units: List<Units>
): Blueprint<Element> {
    override fun new() = Element(
                                    corps,
                                    kind,
                                    rung,
                                    units.map(Units::new).toSet()
                                )

    /**
     * When adding this method to the building process the resulting element will be a [CommandingElement].
     *
     * @return a new [CommandingElementBlueprint] with the given subordinates
     */
    internal infix fun commanding(subordinates: List<Blueprint<Element>>) = CommandingElementBlueprint(corps, kind, rung, units, subordinates)

    operator fun times(i: Int) = List(i) { this.copy() }

    operator fun plus(blueprint: ElementBlueprint) = listOf(this, blueprint)
}

internal operator fun Int.times(blueprint: ElementBlueprint) = blueprint * this

internal fun Collection<Blueprint<Element>>.new(): Set<Element> = map { it.new() }.toSet()