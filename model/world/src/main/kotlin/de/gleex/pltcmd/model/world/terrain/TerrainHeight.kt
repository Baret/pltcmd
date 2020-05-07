package de.gleex.pltcmd.model.world.terrain

import kotlin.random.Random

/**
 * Represents the height of a tile.
 */
enum class TerrainHeight(val value: Int): Comparable<TerrainHeight> {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    companion object {
        fun random(r: Random) = values().random(r)

        fun ofValue(heightValue: Int): TerrainHeight? = values().find { it.value == heightValue }

        /**
         * The highest possible terrain
         */
        val MAX = values().last()
        /**
         * The lowest possible terrain
         */
        val MIN = values().first()
    }

    operator fun plus(heightToAdd: TerrainHeight) = plus(heightToAdd.value)

    operator fun plus(valueToAdd: Int) =
            when (this) {
                TEN -> TEN
                else                                                                     -> values()[ordinal + valueToAdd]
            }

    operator fun minus(heightToSubtract: TerrainHeight) = minus(heightToSubtract.value)

    operator fun minus(valueToSubtract: Int) =
            when (this) {
                ONE -> ONE
                else                                                                     -> values()[ordinal - valueToSubtract]
            }

}

/** Return the average height of all given terrain heights. If the given list is empty no height is provided. */
fun List<TerrainHeight>.average(): TerrainHeight? {
    if (isEmpty()) {
        // prevent division by zero
        return null
    }
    val sumOfValues = sumBy { it.value }
    val averageValue = sumOfValues / size
    return TerrainHeight.ofValue(averageValue)
}
