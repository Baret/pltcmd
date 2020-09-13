package de.gleex.pltcmd.game.engine.attributes.combat

import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue

internal class HealthAttribute(val health: Property<Int> = 100.toProperty()) : Attribute {
    /** @return false if the health reaches 0 */
    val alive: ObservableValue<Boolean> = health.bindTransform { it > 0 }

    /** @return false if the health reaches 0 */
    val isAlive: Boolean
        get() = alive.value

    operator fun minus(receivedDamage: Int) {
        health.transformValue { it - receivedDamage }
    }

    operator fun plus(receivedHealing: Int) {
        health.transformValue { it + receivedHealing }
    }

    infix fun onDeath(callback: () -> Unit) {
        require(isAlive)
        alive.onChange { changed ->
            require(!changed.newValue)
            callback.invoke()
        }
    }
}