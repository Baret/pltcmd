package de.gleex.pltcmd.model.terrain

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
        fun random() = values().random()

        /**
         * The hi
         */
        val MAX = values().last()
        val MIN = values().first()
    }

    operator fun plus(valueToAdd: Int) =
            when (this) {
                TEN  -> TEN
                else -> values()[ordinal + valueToAdd]
            }

    operator fun minus(valueToSubtract: Int) =
            when (this) {
                ONE  -> ONE
                else -> values()[ordinal - valueToSubtract]
            }

}