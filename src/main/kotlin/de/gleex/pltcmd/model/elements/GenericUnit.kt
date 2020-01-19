package de.gleex.pltcmd.model.elements

/** A typed unit with an id that makes it unique across other units of the same type. */
data class GenericUnit(private val type: UnitType, val id: Long = IdCounter.next()) : Unit {
    /** singleton for remembering used ids */
    object IdCounter {
        var count: Long = 0

        fun next(): Long {
            return count++
        }
    }

    override fun isOfType(type: UnitType): Boolean {
        return this.type == type
    }
}