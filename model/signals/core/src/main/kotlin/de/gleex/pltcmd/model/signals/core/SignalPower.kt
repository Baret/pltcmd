package de.gleex.pltcmd.model.signals.core

abstract class SignalPower(val power: Double) {
    abstract fun initialProcessingValue(): Double

    operator fun compareTo(other: SignalPower): Int = power.compareTo(other.power)

    override fun toString(): String = "SignalPower(power=$power)"
}
