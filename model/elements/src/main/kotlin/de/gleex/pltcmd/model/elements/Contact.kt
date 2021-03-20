package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.faction.Faction
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
data class Contact(
    val faction: Maybe<Faction>,
    val corps: Maybe<Corps> = Maybe.empty(),
    val kind: Maybe<ElementKind> = Maybe.empty(),
    val rung: Maybe<Rung> = Maybe.empty(),
    val unitCount: Maybe<Int> = Maybe.empty()
) {

    /**
     * A string containing this element's [corps], [kind] and [rung]. Can be used as relatively short
     * descriptive summary of what this contact is.
     */
    open val description
        get() = "${corps.text()} ${kind.text()} ${rung.text()} of ${faction.text(Faction::name)} (${unitCount.text()} units)"
            .replace(Regex(" +"), " ")

}

/**
 * Returns a text from this [Maybe]. If no mapping function is provided `toString()` will be used on the value.
 * An empty [String] is returned for an empty [Maybe].
 **/
fun <T : Any> Maybe<T>.text(toText: (T) -> String = Any::toString) = map(toText).orElse("")
