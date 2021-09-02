package de.gleex.pltcmd.game.engine.attributes.movement

import de.gleex.pltcmd.util.measure.speed.Speed
import de.gleex.pltcmd.util.measure.speed.Speed.Companion.min
import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * A movement modifier influences movement of an entity. It may for example change the speed or prevent movement altogether.
 *
 * It is [invoke]d with a speed value and returns the modified value.
 */
sealed class MovementModifier : BaseAttribute() {

    /**
     * Takes a speed value and returns a possibly modified value.
     */
    abstract operator fun invoke(speed: Speed): Speed

    /**
     * Mutating modifier that changes the actual value.
     */
    open class Mutator(private val factor: Double) : MovementModifier() {
        override fun invoke(speed: Speed) = speed * factor
    }

    /**
     * When at least one [MovementModifier] of this type is present it means that an entity can not move.
     *
     * The given speed is also reduced to 0.0.
     */
    open class Prevention() : MovementModifier() {
        override fun invoke(speed: Speed) = Speed.ZERO
    }

    /**
     * A speed cap sets a maximum speed. When invoked it returns the given speed except it is higher than [maxSpeed].
     *
     * @param maxSpeed the cap for the speed.
     */
    open class SpeedCap(private val maxSpeed: Speed) : MovementModifier() {
        override fun invoke(speed: Speed) = min(speed, maxSpeed)
    }
}