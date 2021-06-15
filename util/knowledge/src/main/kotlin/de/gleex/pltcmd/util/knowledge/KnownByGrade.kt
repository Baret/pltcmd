package de.gleex.pltcmd.util.knowledge

import org.hexworks.cobalt.datatypes.Maybe

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
    protected fun <R> revealAt(minAmount: KnowledgeGrade, accessor: (T) -> R): Maybe<R> {
        if (revealed >= minAmount) {
            return Maybe.of(accessor(origin))
        }
        return Maybe.empty()
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

}

/**
 * Creates a [KnownByGrade] from this [Any] that is not revealed.
 */
//fun <T : Any, K: KnownByGrade<*, *>> T.nothingKnown() = K(
//    origin = this,
//    KnowledgeGrade.NONE
//)

/**
 * Creates a [KnownByGrade] from this [Any] that is fully revealed.
 */
//fun <T : Any> T.fullyKnown() =
//    nothingKnown()
//        .apply {
//            reveal(KnowledgeGrade.FULL)
//        }

/**
 * Creates a [KnownByGrade] from this [Any] that is either [revealed] or not.
 *
 * @param revealed when `true`, a [known] terrain will be created, [unknown] otherwise.
 */
//fun <T : Any> T.toKnownByGrade(revealed: Double) =
//    KnownByGrade<T, KnownByGrade<T, *>>(
//        origin = this,
//        KnowledgeGrade.of(revealed)
//    )
