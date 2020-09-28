package de.gleex.pltcmd.game.engine.attributes.goals

/**
 * A security halt is primarily done by infantry. When moving a longer distance, an element stops for some minutes to observe
 * its surroundings to preserve its security.
 *
 * @param ticksToHalt number of ticks to execute the [HaltGoal]
 */
data class SecurityHalt(private val ticksToHalt: Int): TimeoutGoal(ticksToHalt, HaltGoal(), { element, _ ->
    HaltGoal.cleanUp(element)
})
