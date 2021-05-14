package de.gleex.pltcmd.model.combat.attack

import de.gleex.pltcmd.util.measure.area.Area
import de.gleex.pltcmd.util.measure.area.squared
import de.gleex.pltcmd.util.measure.area.times
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import de.gleex.pltcmd.util.measure.distance.hundredMeters
import de.gleex.pltcmd.util.measure.distance.times
import kotlin.math.PI
import kotlin.math.atan
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
            return offAt(cmOffAt100m, 1.hundredMeters)
        }

        /** @param cmOffAt500m the diameter of the cone at 500 meter distance. */
        fun at500m(cmOffAt500m: Int): Precision {
            return offAt(cmOffAt500m, 5.hundredMeters)
        }

        /** @param cmOff the diameter of the cone at [range] [Distance]. */
        fun offAt(cmOff: Int, range: Distance): Precision {
            // formula from http://shiny.imbei.uni-mainz.de:3838/shotGroups_AngularSize/#tab-4574-2 / http://ballistipedia.com/index.php?title=Angular_Size
            val mrad = mradsPerCircle * atan(cmOff / (2.0 * (range inUnit DistanceUnit.meters) * 100.0)) /// 1 m = 100 cm range
            return Precision(mrad)
        }
    }

    /**
     * @return the absolute size in same unit as range
     */
    internal fun offsetAt(range: Distance): Distance = tan(mrad / mradsPerCircle) * 2.0 * range

    /**
     * The area of a circle at the given range that is spread with this precision.
     * @return area as squared unit of the given range's unit
     */
    internal fun areaAt(range: Distance): Area = (PI / 4.0) * offsetAt(range).squared()

    /**
     * @return the percentage to hit the given area at the given range with this precision
     */
    fun chanceToHitAreaAt(areaToHit: Area, range: Distance): Double = (areaToHit / areaAt(range)).coerceAtMost(1.0)

}
