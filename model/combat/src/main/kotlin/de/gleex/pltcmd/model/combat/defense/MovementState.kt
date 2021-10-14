package de.gleex.pltcmd.model.combat.defense

/**
 * Determines in which movement the element currently is.
 */
enum class MovementState : DefenseFactor {
    MOVING {
        override val attackReduction = 0.2
    },
    STATIONARY {
        override val attackReduction = 0.0
    }
}
