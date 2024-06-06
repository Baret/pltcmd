package de.gleex.pltcmd.game.application.actors.terrain.model

/**
 * A bitmap with 8 bits. Each bit represents one neighbor of a coordinate. It represents a boolean
 * state of the respective neighboring coordinate/tile.
 */
class NeighborBitmap private constructor(private val bitmap: UByte) {

    companion object {
        /**
         * Factory function that sets the bits for the given [Direction]s.
         */
        fun of(vararg directions: Direction): NeighborBitmap {
            return NeighborBitmap(directions.sumOf { it.byteValue.toInt() }.toUByte())
        }

        private val ZERO: UByte = 0.toUByte()
    }

    fun isNorth(): Boolean {
        return (bitmap and Direction.NORTH.byteValue) != ZERO
    }

    fun isNorthEast(): Boolean {
        return (bitmap and Direction.NORTH_EAST.byteValue) != ZERO
    }

    fun isEast(): Boolean {
        return (bitmap and Direction.EAST.byteValue) != ZERO
    }

    fun isSouthEast(): Boolean {
        return (bitmap and Direction.SOUTH_EAST.byteValue) != ZERO
    }

    fun isSouth(): Boolean {
        return (bitmap and Direction.SOUTH.byteValue) != ZERO
    }

    fun isSouthWest(): Boolean {
        return (bitmap and Direction.SOUTH_WEST.byteValue) != ZERO
    }

    fun isWest(): Boolean {
        return (bitmap and Direction.WEST.byteValue) != ZERO
    }

    fun isNorthWest(): Boolean {
        return (bitmap and Direction.NORTH_WEST.byteValue) != ZERO
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        return "NeighborBitmap(bitmap=${bitmap.toInt().toString(2).padStart(8, '0')})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NeighborBitmap) return false

        if (bitmap != other.bitmap) return false

        return true
    }

    override fun hashCode(): Int {
        return bitmap.hashCode()
    }

    /**
     * Represents the bit for one of the 8 cardinal directions of a neighboring tile.
     */
    enum class Direction(val byteValue: UByte) {
        NORTH(1.toUByte()),
        NORTH_EAST(2.toUByte()),
        EAST(4.toUByte()),
        SOUTH_EAST(8.toUByte()),
        SOUTH(16.toUByte()),
        SOUTH_WEST(32.toUByte()),
        WEST(64.toUByte()),
        NORTH_WEST(128.toUByte())
    }
}