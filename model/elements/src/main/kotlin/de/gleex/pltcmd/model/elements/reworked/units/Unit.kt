package de.gleex.pltcmd.model.elements.reworked.units

import de.gleex.pltcmd.model.elements.reworked.units.blueprint.UnitBlueprint
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
 * Units are backed by a [UnitBlueprint] for memory optimization. All immutable base values of a unit only need to
 * be present once. This class represents the immutable part of a unit. Mutable attributes should be added by the game
 * engine to track its state.
 */
class Unit(private val blueprint: UnitBlueprint) {
    val id = UUIDFactory.randomUUID()
    /**
     * @see [Unit]
     */
    val name: String
        get() = blueprint::class.simpleName?: "Unit"
    /**
     * @see [Unit]
     */
    val personnel
        get() = blueprint.personnel
    /**
     * @see [Unit]
     */
    val personnelMinimum
        get() = blueprint.personnelMinimum
    /**
     * @see [Unit]
     */
    val kind
        get() = blueprint.kind

    /**
     * Checks if the given blueprint is equal to the one of this unit.
     */
    fun isA(unitBlueprint: UnitBlueprint): Boolean = blueprint == unitBlueprint

    override fun toString() = "${blueprint.kind} $name [id=$id]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Unit

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}