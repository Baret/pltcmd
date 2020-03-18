package de.gleex.pltcmd.model.terrain

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
        fun random(r: Random = Random) = values().random(r)

        @Suppress("MemberVisibilityCanBePrivate")
        fun ofValue(heightValue: Int): TerrainHeight? = values().find { it.value == heightValue }

        /**
         * The highest possible terrain
         */
        val MAX = values().last()
        /**
         * The lowest possible terrain
         */
        val MIN = values().first()

        fun average(heights: List<TerrainHeight>): TerrainHeight? {
            if (heights.isEmpty()) {
                // prevent division by zero
                return null
            }
            val sumOfValues = heights.sumBy { it.value }
            val averageValue = sumOfValues / heights.size
            return ofValue(averageValue)
        }
    }

    operator fun plus(heightToAdd: TerrainHeight) = plus(heightToAdd.value)

    operator fun plus(valueToAdd: Int) =
            when (this) {
                TEN  -> TEN
                else -> values()[ordinal + valueToAdd]
            }

    operator fun minus(heightToSubtract: TerrainHeight) = minus(heightToSubtract.value)

    operator fun minus(valueToSubtract: Int) =
            when (this) {
                ONE  -> ONE
                else -> values()[ordinal - valueToSubtract]
            }

}