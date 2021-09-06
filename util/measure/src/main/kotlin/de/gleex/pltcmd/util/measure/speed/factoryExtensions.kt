package de.gleex.pltcmd.util.measure.speed

/**
 * Returns a [Speed] of `this` km/h
 */
val Int.kph: Speed
    get() = this.toDouble().kph

/**
 * Returns a [Speed] of `this` km/h
 */
val Double.kph: Speed
    get() = Speed(this)
