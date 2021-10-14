package de.gleex.pltcmd.model.combat.defense

enum class CoverState : DefenseFactor {
    /** no cover at all on an open field */
    OPEN {
        override val attackReduction = 0.0
    },
    /** Some bushes or other things that partially conceal troops */
    LIGHT {
        override val attackReduction = 0.2
    },
    /** Some hard cover that is either left for short amounts of time or does not fully cover the element */
    MODERATE {
        override val attackReduction = 0.5
    },
    /** hidden completely in a solid bunker */
    FULL {
        override val attackReduction = 0.8
    }
}