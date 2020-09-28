package de.gleex.pltcmd.game.ui.strings

/**
 * A transformation is used by a [FrontendString]. It transforms an object into a human readable string that
 * can be displayed by the UI.
 */
internal typealias Transformation<T> = T.(Format) -> String