package de.gleex.pltcmd.util.knowledge


/**
 * Represents a [Known] whose bits depend on a [KnowledgeGrade].
 *
 * @param origin the knowledge bit that may be known
 * @param [grade] sets the initial state.
 */
abstract class KnownByGrade<T : Any, SELF : KnownByGrade<T, SELF>>(
    override val origin: T,
    private var grade: KnowledgeGrade
) : Known<T, SELF> {

    /**
     * How much information about [origin] is known.
     */
    val revealed: KnowledgeGrade
        get() = grade

    /**
     * Increases the [KnowledgeGrade] if the given is higher.
     */
    fun reveal(newGrade: KnowledgeGrade) {
        if (newGrade > grade) {
            grade = newGrade
        }
    }

    /**
     * Returns the value of the [accessor] depending on the [revealed] value of this [Known]
     */
    fun <R> revealAt(minAmount: KnowledgeGrade, accessor: (T) -> R): R? {
        if (revealed >= minAmount) {
            return accessor(origin)
        }
        return null
    }

    /**
     * Merging a revealed [KnownByGrade] into another one with the same [origin] [reveal]s it.
     *
     * @return this
     */
    override infix fun mergeWith(other: SELF): Boolean {
        if (other.grade > grade && origin == other.origin) {
            reveal(other.grade)
            return true
        }
        return false
    }

    override fun toString(): String {
        return "${javaClass.simpleName}[grade = $grade, origin = $origin]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KnownByGrade<*, *>

        return origin == other.origin &&
                grade == other.grade
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + grade.hashCode()
        return result
    }

}
