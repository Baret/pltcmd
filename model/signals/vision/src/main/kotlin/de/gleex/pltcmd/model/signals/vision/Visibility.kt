package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.SignalStrength

/**
 * Different states that describe how well something is visible. At large distances no details are visible.
 * Ordered from least to most visible.
 */
enum class Visibility {
    NONE,
    POOR,
    GOOD
}

val SignalStrength.visibility: Visibility
    get() {
        val percent = asRatio()
        return when {
            percent >= 0.4 -> Visibility.GOOD
            isAny()        -> Visibility.POOR
            else           -> Visibility.NONE
        }
    }