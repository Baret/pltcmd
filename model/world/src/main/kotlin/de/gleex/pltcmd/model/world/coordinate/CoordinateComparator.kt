package de.gleex.pltcmd.model.world.coordinate

/**
 * Returns a compareTo result of [northing] and [easting] of one coordinate compared to [otherNorthing]
 * and [otherEasting] of another coordinate.
 *
 * This sorts from most south-west to most north-east. Going line wise first east and then north.
 * Example: 2|2, 3|2, 1|3
 *
 * @return The difference of [northing] to [otherNorthing] or if that is 0, the difference of [easting]
 * to [otherEasting].
 *
 * @see CoordinateComparator
 */
internal fun compareCoordinateComponents(northing: Int, easting: Int, otherNorthing: Int, otherEasting: Int): Int {
    val northDiff = northing - otherNorthing
    if (northDiff == 0) {
        return easting - otherEasting
    }
    return northDiff
}