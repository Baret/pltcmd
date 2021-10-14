package de.gleex.pltcmd.model.combat.defense

/**
 * Describes how much an element exepcts enemy troops.
 */
enum class AwarenessState : DefenseFactor {
    CARELESS {
        override val attackReduction = -0.15
    },
    OBSERVING {
        override val attackReduction = 0.1
    },
    ALERTED {
        override val attackReduction = 0.4
    }
}