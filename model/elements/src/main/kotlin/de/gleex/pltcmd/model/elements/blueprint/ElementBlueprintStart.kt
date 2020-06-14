package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.elements.units.Unit

class ElementBlueprintStart(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung
) {
    infix fun consistingOf(units: Set<Unit>) = ElementBlueprint(corps, kind, rung, units)
}

fun a(corps: Corps, kind: ElementKind, rung: Rung) =
        ElementBlueprintStart(corps, kind, rung)