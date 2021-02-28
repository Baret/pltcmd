package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.faction.Faction
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
data class Contact(
    val faction: Maybe<Faction>,
    val corps: Maybe<Corps>,
    val kind: Maybe<ElementKind>,
    val rung: Maybe<Rung>,
    val unitCount: Maybe<Int>
) {

    /**
     * A string containing this element's [corps], [kind] and [rung]. Can be used as relatively short
     * descriptive summary of what this contact is.
     */
    open val description get() = "$corps $kind $rung of $faction ($unitCount units)"

}
