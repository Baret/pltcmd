package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.knowledge.KnownByGrade
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
typealias KnownContact = KnownByGrade<ContactData, *>

// basic information is always available
val KnownContact.kind: ElementKind
    get() = origin.kind
val KnownContact.position: Coordinate
    get() = origin.position

// details of the element depend on how much we know about it
val KnownContact.rung: Maybe<Rung>
    get() = revealAt(KnowledgeGrade.LOW) { it.rung }
val KnownContact.affiliation: Maybe<Affiliation>
    get() = revealAt(KnowledgeGrade.MEDIUM) { it.affiliation }
val KnownContact.unitCount: Maybe<Int>
    get() = revealAt(KnowledgeGrade.HIGH) { it.unitCount }

/**
 * A string containing this element's [kind] and [rung]. Can be used as relatively short
 * descriptive summary of what this contact is.
 */
val KnownContact.description
    get() = "$kind ${rung.text()} of ${affiliation.text()} (${unitCount.text("unknown")} units)"
        .replace(Regex(" +"), " ")

/**
 * Returns a text from this [Maybe]. If no mapping function is provided `toString()` will be used on the value.
 * An empty [String] is returned for an empty [Maybe].
 **/
fun <T : Any> Maybe<T>.text(defaultText: String = "", toText: (T) -> String = Any::toString) = map(toText).orElse(defaultText)
