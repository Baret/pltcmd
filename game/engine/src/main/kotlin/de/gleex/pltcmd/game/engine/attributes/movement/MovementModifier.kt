package de.gleex.pltcmd.game.engine.attributes.movement

import org.hexworks.amethyst.api.Attribute
import kotlin.math.min

/**
 * A movement modifier influences movement of an entity. It may for example change the speed or prevent movement altogether.
 *
 * It is [invoke]d with a speed value in km/h and returns the modified value.
 */
sealed class MovementModifier : Attribute {

    /**
     * Takes a speed value in km/h and returns a possibly modified value.
     */
    abstract operator fun invoke(speedInKph: Double): Double

    /**
     * Mutating modifier that changes the actual value.
     */
    open class Mutator(private val factor: Double) : MovementModifier() {
        override fun invoke(speedInKph: Double) = speedInKph * factor
    }

    /**
     * When at least one [MovementModifier] of this type is present it means that an entity can not move.
     *
     * The given speed is also reduced to 0.0.
     */
    open class Prevention() : MovementModifier() {
        override fun invoke(speedInKph: Double) = 0.0
    }

    /**
     * A speed cap sets a maximum speed. When invoked it returns the given speed except it is higher than [maxSpeedInKph].
     *
     * @param maxSpeedInKph the cap for the speed.
     */
    open class SpeedCap(private val maxSpeedInKph: Double) : MovementModifier() {
        override fun invoke(speedInKph: Double) = min(speedInKph, maxSpeedInKph)
    }
}