package de.gleex.pltcmd.game.options

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.speed.per
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * This object holds constant values that are otherwise "just assumed". When doing calculations regarding "fixed" values
 * these constants should be used instead of local literal values.
 */
object GameConstants {
    /**
     * Constant values regarding information of the application.
     */
    object AppInfo {
        /**
         * Version number of the packaged application or 'devel' if run directly from source code.
         */
        val version: String = GameConstants::class.java.`package`.implementationVersion ?: "devel"
    }

    /**
     * Constant values regarding ingame time.
     */
    object Time {
        /**
         * The amount of ingame time that passes in one tick.
         */
        @OptIn(ExperimentalTime::class)
        val timeSimulatedPerTick = Duration.seconds(60)

    }

    /**
     * Constant values regarding movement in the world.
     */
    object Movement {

        /**
         * To travel one tile of distance in one tick you need this speed.
         *
         * @see Time.timeSimulatedPerTick
         * @see WorldTile.edgeLength
         */
        val speedForOneTileInOneTick = WorldTile.edgeLength per Time.timeSimulatedPerTick

    }
}