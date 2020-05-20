package de.gleex.pltcmd.model.elements.reworked

import org.hexworks.cobalt.core.platform.factory.UUIDFactory
import org.hexworks.cobalt.datatypes.Maybe

open class Element(
        val kind: ElementKind,
        val size: ElementSize,
        units: Set<Unit>,
        superOrdinate: CommandingElement? = null
) {
    val id = UUIDFactory.randomUUID()

    init {
        require(units.isNotEmpty()) {
            "An element must have at least one unit."
        }
    }
    private val _units: MutableSet<Unit> = units.toMutableSet()
    val units: Set<Unit> = _units

    val superOrdinate: Maybe<CommandingElement> = Maybe.ofNullable(superOrdinate)
}