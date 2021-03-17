package de.gleex.pltcmd.util.knowledge

/**
 * Represents a [Known] that is either fully [revealed] or not.
 *
 * @param [isRevealed] sets the initial state.
 */
abstract class KnownByBoolean<T: Any>(isRevealed: Boolean): Known<T> {

    private var _revealed: Boolean = isRevealed

    /**
     * When true, [origin] is the source of information. What happens when [revealed] is false
     * is up to specific implementations.
     */
    val revealed: Boolean
        get() = _revealed

    /**
     * Marks this [KnownByBoolean] as [revealed].
     */
    fun reveal() {
        _revealed = true
    }

}