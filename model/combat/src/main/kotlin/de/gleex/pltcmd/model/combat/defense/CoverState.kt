package de.gleex.pltcmd.model.combat.defense

import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainType.*

/**
 * Defensive state that describes the cover and concealment to hide the defender from the attacker.
 */
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


val Terrain.cover: CoverState
    get() = when (this.type) {
        GRASSLAND     -> CoverState.OPEN
        FOREST        -> CoverState.LIGHT
        HILL          -> CoverState.LIGHT
        MOUNTAIN      -> CoverState.MODERATE
        WATER_DEEP    -> CoverState.MODERATE
        WATER_SHALLOW -> CoverState.LIGHT
    }
