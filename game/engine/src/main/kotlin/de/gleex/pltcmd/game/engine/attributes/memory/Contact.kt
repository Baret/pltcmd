package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.engine.entities.types.reportedFaction
import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.util.knowledge.KnownByBoolean
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Holds information about an [Element] that was reported (directly or indirectly, by vision, sound or assumption).
 * It may not be accurate and mostly incomplete!
 */
typealias Contact = KnownByBoolean<ElementEntity, *>

// basic information is always available
val Contact.faction: Maybe<Faction>
    get() = Maybe.of(origin.reportedFaction.value)
val Contact.corps: Maybe<Corps>
    get() = Maybe.of(origin.element.corps)
val Contact.kind: Maybe<ElementKind>
    get() = Maybe.of(origin.element.kind)

// details of the entity type are only available if seen is clearly visible
val Contact.rung: Maybe<Rung>
    get() = revealed { it.element.rung }
val Contact.unitCount: Maybe<Int>
    get() = revealed { it.element.allUnits.size }

// TODO remember where the element was spotted
val Contact.roughLocation: WorldArea
    get() = WorldArea.EMPTY

private fun <T> Contact.revealed(accessor: (ElementEntity) -> T): Maybe<T> {
    if (revealed) {
        return Maybe.of(accessor(origin))
    }
    return Maybe.empty()
}

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
