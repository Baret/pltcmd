package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.affiliationTo
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.knowledge.KnownByGrade
import org.hexworks.cobalt.databinding.api.extension.fold

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
class KnownContact(private val reporter: ElementEntity, contact: ElementEntity, grade: KnowledgeGrade) :
    KnownByGrade<ElementEntity, KnownContact>(contact, grade) {

    // basic information is always available
    val kind: ElementKind
        get() = origin.element.kind
    val position: Coordinate
        get() = origin.currentPosition
    val affiliation: Affiliation
        get() = revealAt(KnowledgeGrade.MEDIUM) { reporter.affiliationTo(origin) } ?: Affiliation.Unknown

    // details of the element depend on how much we know about it
    val rung: Rung?
        get() = revealAt(KnowledgeGrade.LOW) { it.element.rung }
    val unitCount: Int?
        get() = revealAt(KnowledgeGrade.HIGH) { it.element.totalUnits }

    override fun copy(): KnownContact {
        return KnownContact(reporter, origin, revealed)
    }
}

/**
 * A string containing this element's [KnownContact.kind] and [KnownContact.rung]. Can be used as relatively short
 * descriptive summary of what this contact is.
 */
val KnownContact.description
    get() = "$affiliation $kind ${rung.text()} consisting of ${unitCount.text("unknown")} units"
        .replace(Regex(" +"), " ")

/**
 * Returns a text from this nullable. If no mapping function is provided, `toString()` will be used on the value.
 * [defaultText] (empty string by default) is used when this is null.
 **/
fun <T : Any> T?.text(defaultText: String = "", toText: (T) -> String = Any::toString) = fold({ defaultText }, toText)
