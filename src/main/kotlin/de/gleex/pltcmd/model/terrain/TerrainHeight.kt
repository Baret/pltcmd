package de.gleex.pltcmd.model.terrain

enum class TerrainHeight(val value: Int) {
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

    operator fun inc() =
            when(this) {
                TEN -> TEN
                else -> enumValues<TerrainHeight>()[ordinal+1]
            }

    operator fun dec() =
            when(this) {
                ONE -> ONE
                else -> enumValues<TerrainHeight>()[ordinal-1]
            }
}