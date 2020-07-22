package de.gleex.pltcmd.game.engine.attributes.movement

import org.hexworks.amethyst.api.Attribute
import kotlin.math.min

/**
 * A movement modifier influences movement of an entity. It may for example change the speed or prevent movement altogether.
 */
sealed class MovementModifier : Attribute {

    abstract operator fun invoke(speed: MovementSpeed): Double

    open class Mutator(private val factor: Double) : MovementModifier() {
        override fun invoke(speed: MovementSpeed) = speed.value * factor
    }

    open class Prevention() : MovementModifier() {
        override fun invoke(speed: MovementSpeed) = 0.0
    }

    open class SpeedCap(private val maxSpeedInKph: Double) : MovementModifier() {
        override fun invoke(speed: MovementSpeed) = min(speed.value, maxSpeedInKph)
    }
}