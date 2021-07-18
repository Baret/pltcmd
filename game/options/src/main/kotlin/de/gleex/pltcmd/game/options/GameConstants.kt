package de.gleex.pltcmd.game.options

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.speed.div

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
        const val secondsSimulatedPerTick = 60.0

        /**
         * The number of ticks needed to simulate one ingame minute.
         * @see secondsSimulatedPerTick
         */
        const val ticksPerMinute: Double = 60.0 / secondsSimulatedPerTick
    }

    /**
     * Speed is distance per time ;)
     */
    object Speed {
        private const val secondsPerHour = 60.0 * 60.0
        private const val metersPerKilometer = 1000.0

        /**
         * To travel one tile of distance in one tick you need this speed.
         *
         * @see Time.secondsSimulatedPerTick
         * @see World.tileSizeInMeters
         */
        val speedForOneTileInOneTickInMetersPerSecond = Coordinate.edgeLength / Time.secondsSimulatedPerTick

        /**
         * To travel one tile of distance in one tick you need this speed.
         *
         * @see Time.secondsSimulatedPerTick
         * @see World.tileSizeInMeters
         */
        val speedForOneTileInOneTickInKph =
                // convert meters to kilometers and seconds to hours -> m/s to km/h
                speedForOneTileInOneTickInMetersPerSecond * (secondsPerHour / metersPerKilometer)
    }
}