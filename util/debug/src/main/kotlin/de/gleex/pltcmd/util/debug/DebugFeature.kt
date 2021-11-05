package de.gleex.pltcmd.util.debug

/**
 * Defines something as a "debug feature".
 *
 * Things annotated as debug feature are likely to be unpolished and are used for development purposes. Most
 * probably a debug feature is not intended as actual game content.
 *
 * They may be removed later, although some things may also be considered real features after being a debug
 * feature first.
 */
annotation class DebugFeature(
    /**
     * Optionally describes the intention of this debug feature and how long it might stay.
     */
    val description: String = "")
