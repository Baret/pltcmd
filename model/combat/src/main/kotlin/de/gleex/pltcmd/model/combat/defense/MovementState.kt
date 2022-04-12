package de.gleex.pltcmd.model.combat.defense

/**
 * Describes the type of movement an actor currently executes.
 */
enum class MovementState : DefenseFactor {
    MOVING {
        override val attackReduction = 0.2
    },
    STATIONARY {
        override val attackReduction = 0.0
    }
}
