package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new

/**
 * Base class for the element blueprints.
 */
sealed class AbstractElementBlueprint<T : Element>(
        val corps: Corps,
        val kind: ElementKind,
        val rung: Rung,
        val units: List<Units>
) : Blueprint<T> {

    abstract operator fun times(i: Int): List<AbstractElementBlueprint<T>>

    operator fun plus(blueprint: AbstractElementBlueprint<*>): List<AbstractElementBlueprint<out Element>> =
            listOf(this, blueprint)

    operator fun plus(blueprints: Collection<AbstractElementBlueprint<*>>): List<AbstractElementBlueprint<out Element>> =
            listOf(this, *blueprints.toTypedArray())

    /**
     * Returns a copy of this blueprint with the given [ElementKind].
     */
    abstract infix fun withKind(newKind: ElementKind): AbstractElementBlueprint<T>

    /**
     * Returns a copy of this blueprint with the given [Corps].
     */
    abstract infix fun withCorps(newCorps: Corps): AbstractElementBlueprint<T>

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

    override fun toString(): String {
        return "${this::class.simpleName}(" +
                "corps=$corps, " +
                "kind=$kind, " +
                "rung=$rung, " +
                "units=$units" +
                ")"
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
        units: List<Units>
) : AbstractElementBlueprint<Element>(corps, kind, rung, units) {

    override fun new() = Element(
            corps,
            kind,
            rung,
            units.map(Units::new)
                    .toSet()
    )

    override fun times(i: Int) =
            List(i) { ElementBlueprint(corps, kind, rung, units) }

    override fun withKind(newKind: ElementKind) = ElementBlueprint(corps, newKind, rung, units)

    override fun withCorps(newCorps: Corps) = ElementBlueprint(newCorps, kind, rung, units)
}

/**
 * A blueprint to create new [CommandingElement]s.
 */
class CommandingElementBlueprint(
        corps: Corps,
        kind: ElementKind,
        rung: Rung,
        units: List<Units>,
        val subordinates: List<AbstractElementBlueprint<*>>
) : AbstractElementBlueprint<CommandingElement>(corps, kind, rung, units) {

    override fun new() = CommandingElement(corps, kind, rung, units.new(), subordinates.new())

    override fun withKind(newKind: ElementKind) =
            CommandingElementBlueprint(corps, newKind, rung, units, subordinates.map { it.withKind(newKind) })

    override fun withCorps(newCorps: Corps) =
            CommandingElementBlueprint(newCorps, kind, rung, units, subordinates.map { it.withCorps(newCorps) })

    override operator fun times(i: Int) =
            List(i) { CommandingElementBlueprint(corps, kind, rung, units, subordinates) }

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

internal operator fun Int.times(blueprint: AbstractElementBlueprint<*>) = blueprint * this

fun Collection<AbstractElementBlueprint<*>>.new(): Set<Element> = map { it.new() }.toSet()

