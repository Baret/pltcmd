package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.engine.entities.types.reportedFaction
import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.knowledge.KnownByGrade
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
typealias Contact = KnownByGrade<ElementEntity, *>

// basic information is always available
val Contact.corps: Maybe<Corps>
    get() = Maybe.of(origin.element.corps)
val Contact.kind: Maybe<ElementKind>
    get() = Maybe.of(origin.element.kind)

// details of the element depend on how much we know about it
val Contact.rung: Maybe<Rung>
    get() = revealAt(KnowledgeGrade.LOW) { it.element.rung }
val Contact.faction: Maybe<Faction>
    get() = revealAt(KnowledgeGrade.MEDIUM) { it.reportedFaction.value }
val Contact.unitCount: Maybe<Int>
    get() = revealAt(KnowledgeGrade.HIGH) { it.element.allUnits.size }

// TODO remember where the element was spotted
val Contact.roughLocation: WorldArea
    get() = WorldArea.EMPTY

/**
 * A string containing this element's [corps], [kind] and [rung]. Can be used as relatively short
 * descriptive summary of what this contact is.
 */
val Contact.description
    get() = "${corps.text()} ${kind.text()} ${rung.text()} of ${faction.text(Faction::name)} (${unitCount.text()} units)"
        .replace(Regex(" +"), " ")

/**
 * Returns a text from this [Maybe]. If no mapping function is provided `toString()` will be used on the value.
 * An empty [String] is returned for an empty [Maybe].
 **/
fun <T : Any> Maybe<T>.text(toText: (T) -> String = Any::toString) = map(toText).orElse("")
