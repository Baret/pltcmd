package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.blueprints.unit.UnitBlueprint
import org.hexworks.cobalt.core.platform.factory.UUIDFactory

/**
 * A _unit_ is one independent piece of "military equipment" (i.e. a soldier or a weapon system or a vehicle)
 * on the field. It has a unique ID and the following fields:
 *
 * [name] describing this unit, for example "Rifleman".
 *
 * [personnel] is the default number of soldiers that operate this unit at full strength.
 *
 * [personnelMinimum] is the number of soldiers needed to keep this unit operational.
 *
 * [kind] see [UnitBlueprint.kind]
 *
 * Units are backed by a [UnitBlueprint] for memory optimization. All immutable base values of a unit only need to
 * be present once. This class represents the immutable part of a unit. Mutable attributes should be added by the game
 * engine to track its state.
 */
class Unit(val blueprint: UnitBlueprint) {
    val id = UUIDFactory.randomUUID()
    val name: String
        get() = blueprint::class.objectInstance?.javaClass?.simpleName?: "Unit"
    val personnel
        get() = blueprint.personnel
    val personnelMinimum
        get() = blueprint.personnelMinimum
    val kind
        get() = blueprint.kind

    fun isA(unitBlueprint: UnitBlueprint): Boolean = blueprint == unitBlueprint

    override fun toString() = "${blueprint.kind} $name [id=$id]"
}