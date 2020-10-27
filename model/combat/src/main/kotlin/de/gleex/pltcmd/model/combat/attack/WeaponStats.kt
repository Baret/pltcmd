package de.gleex.pltcmd.model.combat.attack

/**
 * Data that describes what a type of weapon is able to do.
 * @param roundsPerMinute how many shots this weapon can do per minute including aiming and reloads ([sustained rate](https://en.wikipedia.org/wiki/Rate_of_fire#Sustained_or_effective_rate))
 * @param shotAccuracy the probability for a single shot to hit the typically aimed for target at a typical range for the weapon category (hand gun, mounted, indirect)
 */
data class WeaponStats(val roundsPerMinute: Int, val shotAccuracy: Double)