package de.gleex.pltcmd.util.measure.compass.points

import de.gleex.pltcmd.util.measure.compass.bearing.toBearing

/**
 * The cardinal and intercardinal points of the compass.
 */
enum class CardinalPoint(angle: Int) {
    /** North */
    N(0),
    /** Northeast */
    NE(45),
    /** East */
    E(90),
    /** South */
    SE(135),
    /** South */
    S(180),
    /** Southwest */
    SW(225),
    /** West */
    W(270),
    /** Northwest */
    NW(315);

    /**
     * The bearing of this cardinal point. When a bearing has this value it is "due north" for example.
     */
    val bearing = angle.toBearing()
}