package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.combat.Weapon
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/** The offensive part of the given element. */
internal class ShootersAttribute(weapons: Iterable<Weapon>) : Attribute {
    constructor(element: Element) : this(element.allUnits.map { it.blueprint.weapon })

    internal val shooters: List<Shooter> = weapons.map { Shooter(it) }
}

/** A single shooter with a weapon that can shoot at a target. Holds the state of partial shots between multiple calls. */
internal class Shooter(private val weapon: Weapon) {

    companion object {
        private val log = LoggerFactory.getLogger(Shooter::class)
    }

    // remember "half shots" for if a shot is done after a longer time period than given in a single call
    private var partialShot: Double = 0.0

    /** @return damage of all hits in the given time */
    @ExperimentalTime
    fun fireShots(attackDuration: Duration, random: Random): Int {
        val shotsPerDuration = weapon.roundsPerMinute * attackDuration.inMinutes + partialShot
        // rounding down loses a partial shot that is remember for the next call.
        partialShot = shotsPerDuration.rem(1)
        val fullShots: Int = shotsPerDuration.toInt()
        var hits = 0
        repeat(fullShots) {
            if (random.nextDouble() <= weapon.shotAccuracy) {
                hits = hits.inc()
            }
        }
        log.info("firing $shotsPerDuration shots in $attackDuration with accuracy ${weapon.shotAccuracy} results in $hits hits")
        return hits * 1 // dmg / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
    }

}
