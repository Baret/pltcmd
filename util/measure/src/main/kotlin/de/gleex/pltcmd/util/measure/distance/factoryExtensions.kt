package de.gleex.pltcmd.util.measure.distance

/**
 * Creates a [Distance] of this meters length.
 */
val Int.meters: Distance
    get() = Distance(DistanceUnit.meters inMeters this)

/**
 * Creates a [Distance] of this meters length.
 *
 * As [Distance] only supports full meters this double is rounded!
 */
val Double.meters: Distance
    get() = Distance(this * DistanceUnit.meters.factorToMeters)

/**
 * Creates a [Distance] of this hundred meters length.
 */
val Int.hundredMeters: Distance
    get() = Distance(DistanceUnit.hundredMeters inMeters this)

/**
 * Creates a [Distance] of this hundred meters length rounded to full meters.
 */
val Double.hundredMeters: Distance
    get() = Distance(this * DistanceUnit.hundredMeters.factorToMeters)

/**
 * Creates a [Distance] of this kilometers length.
 */
val Int.kilometers: Distance
    get() = Distance(DistanceUnit.kilometers inMeters this)

/**
 * Creates a [Distance] of this kilometers length rounded to full meters.
 */
val Double.kilometers: Distance
    get() = Distance(this * DistanceUnit.kilometers.factorToMeters)