package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Unit

class ElementBlueprint(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung,
        private val units: Set<Unit>
): Blueprint<Element> {
    private var subordinates: List<ElementBlueprint>? = null

    override fun new() = Element(corps, kind, rung, units)

    infix fun commanding(subordinates: Set<Element>) = CommandingElementBlueprint(corps, kind, rung, units, subordinates)

    operator fun times(i: Int) = List(i) { new() }.toSet()

    operator fun plus(blueprint: ElementBlueprint) = setOf(this.new(), blueprint.new())
}

operator fun Int.times(blueprint: ElementBlueprint) = blueprint * this