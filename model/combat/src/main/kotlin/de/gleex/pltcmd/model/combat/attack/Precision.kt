package de.gleex.pltcmd.model.combat.attack

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.tan

/**
 * "Precision is like a cone of error that projects out from the muzzle of the gun."
 * (http://ballistipedia.com/index.php?title=Describing_Precision @2020-09-29)
 * This is an angle for such a cone. It is measured in [milliradian](https://en.wikipedia.org/wiki/Milliradian)
 * (mrad, a thousandth of a radian). A mrad is ≈ 0.057296 degrees.
 *
 * @param cmOffAt100m the diameter of the cone at 100 meter distance.
 */
class Precision(cmOffAt100m: Int) {

    companion object {
        const val mradsPerCircle = 2000.0
    }

    private val mrad = mradsPerCircle * atan(cmOffAt100m / (2.0 * 10000.0)) /// 10000 cm = 100 m range

    /**
     * @return the absolute size in same unit as range
     */
    fun offsetAt(range: Double): Double = 2 * range * tan(mrad / mradsPerCircle)

    /**
     * The area of a circle at the given range that is spread with this precision.
     * @return area as squared unit of the given range's unit
     */
    fun areaAt(range: Double): Double = PI / 4.0 * offsetAt(range).pow(2.0)

    /**
     * @return the percentage to hit the given area at the given range with this precision
     */
    fun chanceToHitAreaAt(areaToHit: Double, range: Double): Double = (areaToHit / areaAt(range)).coerceAtMost(1.0)

}
