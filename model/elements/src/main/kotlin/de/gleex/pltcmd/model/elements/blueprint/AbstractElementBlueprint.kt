package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new

/**
 * Base class for the element blueprints.
 */
sealed class AbstractElementBlueprint<T: Element>(
        internal val corps: Corps,
        internal val kind: ElementKind,
        internal val rung: Rung,
        internal val units: List<Units>
): Blueprint<T> {
    /**
     * When adding this method to the building process the resulting element will be a [CommandingElement].
     *
     * @return a new [CommandingElementBlueprint] with the given subordinates
     */
    internal open infix fun commanding(subordinates: List<AbstractElementBlueprint<*>>) =
            CommandingElementBlueprint(corps, kind, rung, units, subordinates)

    // generated equals and hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractElementBlueprint<*>) return false

        if (corps != other.corps) return false
        if (kind != other.kind) return false
        if (rung != other.rung) return false
        if (units != other.units) return false

        return true
    }

    override fun hashCode(): Int {
        var result = corps.hashCode()
        result = 31 * result + kind.hashCode()
        result = 31 * result + rung.hashCode()
        result = 31 * result + units.hashCode()
        return result
    }
}

/**
 * A blueprint used to create new instances of [Element].
 *
 * To create instances of [CommandingElement] use a [CommandingElementBlueprint].
 */
class ElementBlueprint(
        corps: Corps,
        kind: ElementKind,
        rung: Rung,
        /**
         * Visible for testing.
         */
        units: List<Units>
) : AbstractElementBlueprint<Element>(corps, kind, rung, units) {

    override fun new() = Element(
            corps,
            kind,
            rung,
            units.map(Units::new)
                    .toSet()
    )

    operator fun times(i: Int) = List(i) { ElementBlueprint(corps, kind, rung, units) }

    operator fun plus(blueprint: ElementBlueprint) = listOf(this, blueprint)
}

/**
 * A blueprint to create new [CommandingElement]s.
 */
class CommandingElementBlueprint(
        corps: Corps,
        kind: ElementKind,
        rung: Rung,
        units: List<Units>,
        internal val subordinates: List<AbstractElementBlueprint<*>>
) : AbstractElementBlueprint<CommandingElement>(corps, kind, rung, units) {

    override fun new() = CommandingElement(corps, kind, rung, units.new(), subordinates.new())

    override fun commanding(subordinates: List<AbstractElementBlueprint<*>>): CommandingElementBlueprint {
        return super.commanding(subordinates + this.subordinates)
    }

    operator fun times(i: Int) =
            List(i) { CommandingElementBlueprint(corps, kind, rung, units, subordinates) }

    operator fun plus(list: List<AbstractElementBlueprint<*>>): List<AbstractElementBlueprint<*>> = list + this

    // generated equals and hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommandingElementBlueprint) return false
        if (!super.equals(other)) return false

        if (subordinates != other.subordinates) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + subordinates.hashCode()
        return result
    }


}

internal operator fun Int.times(blueprint: CommandingElementBlueprint) = blueprint * this

internal operator fun Int.times(blueprint: ElementBlueprint) = blueprint * this

internal fun Collection<AbstractElementBlueprint<*>>.new(): Set<Element> = map { it.new() }.toSet()

