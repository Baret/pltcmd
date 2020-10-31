package de.gleex.pltcmd.model.combat.attack

/**
 * Data that describes what a type of weapon is able to do.
 * @param roundsPerMinute how many shots this weapon can do per minute including aiming and reloads ([sustained rate](https://en.wikipedia.org/wiki/Rate_of_fire#Sustained_or_effective_rate))
 * @param shotAccuracy the spread for consecutive shots
 */
data class WeaponStats(val roundsPerMinute: Int, val shotAccuracy: Precision)
