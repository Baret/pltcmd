package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.faction.FactionRelations
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * This file contains code for entities that have the [FactionAttribute].
 */

/** Type marker for entities that have the FactionAttribute */
interface Factionable : EntityType
typealias FactionEntity = GameEntity<Factionable>

/** Access to the [Property] of the [FactionAttribute] of a [FactionEntity] */
val FactionEntity.faction: ObservableValue<Faction>
    get() = getAttribute(FactionAttribute::class).reportedFaction

fun FactionEntity.affiliationTo(other: FactionEntity): Affiliation =
    affiliationTo(other.faction.value)

fun FactionEntity.affiliationTo(other: Faction): Affiliation =
    FactionRelations[faction.value, other]

/**
 * Invokes [whenFactionable] if this entity is an [FactionEntity]. When the type is not [Factionable],
 * null is returned.
 *
 * @param R the type that is returned by [whenFactionable]
 */
fun <R> AnyGameEntity.asFactionEntity(whenFactionable: (FactionEntity) -> R): R? =
    tryCastTo<FactionEntity, Factionable, R>(whenFactionable)
