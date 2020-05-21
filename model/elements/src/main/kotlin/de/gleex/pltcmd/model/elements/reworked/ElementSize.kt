package de.gleex.pltcmd.model.elements.reworked

/**
 * An [Element]'s size determines its position in the military hierarchy. An element can
 * only be commanded by a bigger element and vice versa.
 *
 * The order of this enum is ascending, meaning [Fireteam] < [Squad] etc. Or see the ordinal value.
 */
enum class ElementSize {
    /**
     * The smallest element worth mentioning. It is the smallest element usually making up a [Squad].
     */
    Fireteam,
    /**
     * A squad has enough fire power and organizational value to be represented on the command net.
     * It is capable of accomplishing simple goals independently.
     */
    Squad,
    /**
     * A platoon is usually made up of several [Squad]s and the ideal mix between having quite some punch and
     * at the same time being an element independent enough to complete most missions a commander may have in the field.
     */
    Platoon,
    /**
     * Companies usually group 3 to 6 [Platoon]s and may be used to accomplish a mission in the field but are
     * rather unlikely to. They are usually too cumbersome to maneuver effectively in fight and should only
     * be used to move big numbers of troops around the map.
     */
    Company,
    /**
     * A battalion is a huge group of units in context of this game. It is unlikely to ever exist except as
     * an organizational bucket to keep lots of elements in reserve. Most probably this size is just out of
     * scope of the player's paycheck ;)
     */
    Battalion
}
