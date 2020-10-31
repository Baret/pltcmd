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
 */
class Precision(private val mrad: Double) {

    companion object {
        const val mradsPerCircle = 2000.0

        /** @param cmOffAt100m the diameter of the cone at 100 meter distance. */
        fun at100m(cmOffAt100m: Int): Precision {
            return offAt(cmOffAt100m, 100)
        }

        /** @param cmOffAt500m the diameter of the cone at 500 meter distance. */
        fun at500m(cmOffAt500m: Int): Precision {
            return offAt(cmOffAt500m, 500)
        }

        /** @param cmOff the diameter of the cone at [rangeInM] distance. */
        fun offAt(cmOff: Int, rangeInM: Int): Precision {
            // formula from http://shiny.imbei.uni-mainz.de:3838/shotGroups_AngularSize/#tab-4574-2 / http://ballistipedia.com/index.php?title=Angular_Size
            val mrad = mradsPerCircle * atan(cmOff / (2.0 * rangeInM * 100.0)) /// 1 m = 100 cm range
            return Precision(mrad)
        }
    }

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
