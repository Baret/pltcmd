package de.gleex.pltcmd.model.mapgeneration.extensions

import kotlin.random.Random
import kotlin.random.asJavaRandom


/**
 * Returns a normally distributed random number around the given mean that lies between the given minimum and maximum.
 * The standard deviation is chosen to almost fill the area under the curve in the given range if the mean is at the edge of the given range.
 * Due to the restriction of a fixed range the edges will have a higher probability as all values out of range will use the min/max value.
 */
fun Random.normalDistributedInRange(mean: Double, min: Int, max: Int): Int {
    return normalDistributedInRange(mean, min.toDouble(), max.toDouble()).toInt()
}

/**
 * Returns a normally distributed random number around the given mean that lies between the given minimum and maximum.
 * The standard deviation is chosen to almost fill the area under the curve in the given range if the mean is at the edge of the given range.
 * Due to the restriction of a fixed range the edges will have a higher probability as all values out of range will use the min/max value.
 */
fun Random.normalDistributedInRange(mean: Double, min: Double, max: Double): Double {
    val range = max - min
    // 3.0 = one third of the range still fills half of the bell/curve, so for a mean at min/max the other extreme value is still possible
    // use http://onlinestatbook.com/2/calculators/normal_dist.html for visualization
    val randomValue = normalDistributed(mean, range / 3.0)
    return randomValue.coerceIn(min, max)
}

/**
 * Returns a normally distributed random number around the given mean (zero by default) and standard deviation (one by default).
 */
fun Random.normalDistributed(mean: Double = 0.0, stdDeviation: Double = 1.0): Double {
    val gaussian = asJavaRandom().nextGaussian()
    return mean + gaussian * stdDeviation
}
