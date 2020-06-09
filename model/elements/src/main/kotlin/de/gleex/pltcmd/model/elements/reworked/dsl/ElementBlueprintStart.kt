package de.gleex.pltcmd.model.elements.reworked.dsl

import de.gleex.pltcmd.model.elements.reworked.Corps
import de.gleex.pltcmd.model.elements.reworked.ElementKind
import de.gleex.pltcmd.model.elements.reworked.Rung
import de.gleex.pltcmd.model.elements.reworked.units.Unit

class ElementBlueprintStart(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung
) {
    infix fun consistingOf(units: Set<Unit>) = ElementBlueprint(corps, kind, rung, units)
}

fun a(corps: Corps, kind: ElementKind, rung: Rung) =
        ElementBlueprintStart(corps, kind, rung)