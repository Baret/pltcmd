package de.gleex.pltcmd.model.combat.defense

/**
 * Describes a fact that is considered for defense. So it is able to reduce the chance of being shot by an attack.
 **/
interface DefenseFactor {
    /** A ratio which describes the chance for an attacker to miss a careful shot. Negative values may be used to indicate that other factory are reduced by this factor. */
    val attackReduction: Double
}